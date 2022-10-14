package at.ac.tuwien.sepm.groupphase.backend.service.image.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.image.ImageService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ImageServiceImpl implements ImageService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public String imageToBase64(String imageRef) {
    LOGGER.info("Find EventImage with this imageRef:{}", imageRef);
    File file;
    byte[] fileContent = null;
    if (imageRef != null) {
      file = findImageByImageRef(imageRef).orElse(null);
      if (file != null) {
        try {
          fileContent = FileUtils.readFileToByteArray(file);
        } catch (FileNotFoundException e) {
          return "";
        } catch (IOException e) {
          return "";
        }
      }
    }
    String base64 = "";
    if (fileContent != null) {
      base64 = Base64.getEncoder().encodeToString(fileContent);
    }
    return base64;
  }

  private Optional<File> findImageByImageRef(String imageRef) {
    try {
      ClassLoader classLoader = getClass().getClassLoader();
      return Optional.of(
          new File(
              (classLoader.getResource("images").getFile() + "/" + imageRef)
                  .replaceAll("%20", " ")));
    } catch (RuntimeException e) {
      return Optional.empty();
    }
  }

  @Override
  public void store(MultipartFile multipartFile, String savedName) {
    try {
      ClassLoader classLoader = getClass().getClassLoader();
      File file =
          new File(
              (classLoader.getResource("images").getFile() + "/" + savedName)
                  .replaceAll("%20", " "));
      if (file.createNewFile()) {
        multipartFile.transferTo(file);
      } else {
        throw new ResponseStatusException(HttpStatus.CONFLICT);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (NullPointerException e) {
      throw new ResponseStatusException(HttpStatus.INSUFFICIENT_STORAGE);
    }
  }
}
