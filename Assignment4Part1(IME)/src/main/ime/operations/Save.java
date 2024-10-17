package ime.operations;

import java.awt.image.BufferedImage;

import ime.imageIO.ImageLibrary;
import ime.imageIO.ImageWriter;
import ime.models.Image;
import ime.models.Pixel;

public class Save extends ImageOperationManager {

  public Save(ImageLibrary imageLibrary) {
    super(imageLibrary);
  }

  @Override
  public void apply(String[] args) throws IllegalArgumentException {
    validateArgs(args);
    String imagePath = args[0];
    String[] parts = imagePath.split("\\.");
    String imageFormat = parts[parts.length - 1];
    String imageName = args[1];
    Image simpleImage = getImage(imageName);
    BufferedImage bufferedImage = convertToBufferedImage(simpleImage);
    ImageWriter.writeImage(bufferedImage, imageFormat, imagePath);
  }

  /**
   * This method converts image to buffered image representation.
   *
   * @param image the image which has to be converted to buffered image representation.
   * @return the converted image.
   */
  private BufferedImage convertToBufferedImage(Image image) {
    int width = image.getWidth();
    int height = image.getHeight();
    int imageType = image.getType().getImageType();
    BufferedImage bufferedImage = new BufferedImage(width, height, imageType);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Pixel pixel = image.getPixel(i, j);
        if (pixel != null) {
          bufferedImage.setRGB(j, i, pixel.getColorComponents());
        }
      }
    }
    return bufferedImage;
  }

}
