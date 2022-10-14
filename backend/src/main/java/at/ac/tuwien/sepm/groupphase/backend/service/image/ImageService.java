package at.ac.tuwien.sepm.groupphase.backend.service.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  /**
   * Stores a single file.
   *
   * @param file to store
   */
  void store(MultipartFile file, String savedName);

  /**
   * Converts a if it exists with the event imageRef of the event.
   *
   * @param imageRef is the image name
   * @return the image to the event
   */
  String imageToBase64(String imageRef);
}
