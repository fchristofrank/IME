package ime.controller;

/**
 * The Features interface defines the contract for image manipulation operations.
 * It provides methods for loading, modifying, and saving images, as well as
 * managing undo/redo functionality and preview modes.
 */
public interface Features {

  /**
   * Loads an image from the specified path.
   *
   * @param imagePath    the path to the image file
   * @param userDecision a boolean indicating user's decision (purpose not specified)
   * @return true if the operation is applied successfully.
   */
  boolean loadImage(String imagePath, boolean userDecision);

  /**
   * Flips the loaded image based on the specified flip type.
   *
   * @param flipType the type of flip operation to perform
   * @return true if the operation is applied successfully.
   */
  boolean flipImage(String flipType);

  /**
   * Applies a filter to the loaded image.
   *
   * @param isPreview  boolean indicating whether this is a preview operation
   * @param filterType the type of filter to apply
   * @param splitWidth the width at which to split the preview (if applicable)
   * @return true if the operation is applied successfully.
   */
  boolean applyFilter(boolean isPreview, String filterType, String splitWidth);

  /**
   * Applies a grayscale effect to the loaded image.
   *
   * @param isPreview     boolean indicating whether this is a preview operation
   * @param grayScaleType the type of grayscale effect to apply
   * @param splitWidth    the width at which to split the preview (if applicable)
   * @return true if the operation is applied successfully.
   */
  boolean applyGreyScale(boolean isPreview, String grayScaleType, String splitWidth);

  /**
   * Applies color correction to the loaded image.
   *
   * @param isPreview  boolean indicating whether this is a preview operation
   * @param splitWidth the width at which to split the preview (if applicable)
   * @return true if the operation is applied successfully.
   */
  boolean applyColorCorrect(boolean isPreview, String splitWidth);

  /**
   * Compresses the loaded image.
   *
   * @param compressionRatio the ratio at which to compress the image
   * @return true if the operation is applied successfully.
   */
  boolean applyCompress(String compressionRatio);

  /**
   * Adjusts the levels of the loaded image.
   *
   * @param isPreview boolean indicating whether this is a preview operation
   * @param args      variable number of arguments for level adjustment
   * @return true if the operation is applied successfully.
   */
  boolean adjustLevels(boolean isPreview, String... args);

  /**
   * Saves the current image to the specified path.
   *
   * @param imagePath the path where the image should be saved
   * @return true if the operation is applied successfully.
   */
  boolean saveImage(String imagePath);

  /**
   * Undoes the last operation performed on the image.
   */
  void undo();

  /**
   * Redoes the last undone operation on the image.
   */
  void redo();

  /**
   * Toggles the preview mode for image operations.
   *
   * @param splitWidth the width at which to split the preview
   */
  void togglePreview(String splitWidth);

  /**
   * Exits the preview mode.
   */
  void exitPreviewMode();

  void applyPreviewChanges();

  /**
   * Checks if an image is both loaded and saved.
   *
   * @return true if an image is loaded and saved, false otherwise
   */
  boolean isLoadedAndSaved();

  boolean downScale(String width, String height);
}