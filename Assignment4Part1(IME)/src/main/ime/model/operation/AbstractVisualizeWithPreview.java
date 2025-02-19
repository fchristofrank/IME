package ime.model.operation;

import ime.model.image.Image;
import ime.model.pixel.Pixel;
import ime.model.pixel.PixelFactory;

/**
 * This is an extended feature of Visualize methods. The new feature required no code change to
 * existing tested code. This extends the Visualize function and process the operation for the given
 * width only.
 */
public abstract class AbstractVisualizeWithPreview extends AbstractVisualize {

  @Override
  protected void processImage(Image inputImage, Pixel[][] pixels, String... args) {

    int widthSplitPercentage = 100;
    int heightSplitPercentage = 100;
    if (args.length != 0) {
      if (args[1] != null && !args[1].isEmpty()) {
        widthSplitPercentage = Integer.parseInt(args[1]);
      }
    }
    int height = inputImage.getHeight();
    int width = inputImage.getWidth();
    int splitWidth = (inputImage.getWidth() * widthSplitPercentage) / 100;
    int splitHeight = (inputImage.getHeight() * heightSplitPercentage) / 100;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (i < splitHeight && j < splitWidth) {
          int colorValue = getColorComponent(inputImage.getPixel(i, j));
          pixels[i][j] =
              PixelFactory.createPixel(inputImage.getType(), colorValue, colorValue, colorValue);
        } else {
          pixels[i][j] = inputImage.getPixel(i, j);
        }
      }
    }
  }
}
