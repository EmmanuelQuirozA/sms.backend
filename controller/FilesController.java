package com.monarchsolutions.sms.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FilesController {
  @Value("${app.upload-dir}")
  private String uploadDir;

  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @GetMapping("/api/protectedfiles/{filename:.+}")
  public ResponseEntity<Resource> serveProtectedFile(
      @PathVariable String filename,
      HttpServletRequest request
  ) {
    try {
      // 1) Resolve path safely
      Path filePath = Paths.get(uploadDir).resolve(filename).normalize();

      // 2) Load as Resource
      Resource resource = new UrlResource(filePath.toUri());
      if (!resource.exists() || !resource.isReadable()) {
        return ResponseEntity.notFound().build();
      }

      // 3) Determine content type
      String contentType = request.getServletContext()
          .getMimeType(resource.getFile().getAbsolutePath());
      if (contentType == null) {
        contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
      }

      // 4) Return as attachment
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION,
                  "attachment; filename=\"" + resource.getFilename() + "\"")
          .body(resource);

    } catch (MalformedURLException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (IOException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
