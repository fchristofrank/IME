package ime.operations;

import ime.models.Image;
import ime.models.ImageType;
import ime.models.Pixel;
import ime.models.PixelFactory;
import ime.models.SimpleImage;

import static ime.constants.Constants.PIXEL_LOWER_LIMIT;
import static ime.constants.Constants.PIXEL_UPPER_LIMIT;

public abstract class Filter implements ImageOperation {

  protected abstract float[][] getKernel();

  protected abstract int getStartIndexForRow();

  protected abstract int getLastIndexForRow(int width);

  protected abstract int getStartIndexForColumn();

  protected abstract int getLastIndexForColumn(int height);

  public Image apply(Image inputImage, String[] args) throws IllegalArgumentException {

    Image outputImage = new SimpleImage(inputImage.getHeight(), inputImage.getWidth(), ImageType.RGB);
    /*Filter operations applies the matrix on the pixel such that the kernel is inside the image array*/
    for (int i = getStartIndexForRow(); i < getLastIndexForRow(inputImage.getWidth()); i++) {
      for (int j = getStartIndexForColumn(); j < getLastIndexForColumn(inputImage.getHeight()) - 1; j++) {
        System.out.println(inputImage.getPixel(i, j).getRed());
        Pixel newPixel = applyFilterToPixel(inputImage, i, j);
        outputImage.setPixel(i, j, newPixel);
      }
    }
    return outputImage;
  }

  private Pixel applyFilterToPixel(Image image, int x, int y) {

    double redSum = 0;
    double greenSum = 0;
    double blueSum = 0;

    float[][] blurKernel = getKernel();

    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        Pixel newPixel = image.getPixel(x + i, y + j);
        redSum += newPixel.getRed() * blurKernel[i + 1][j + 1];
        greenSum += newPixel.getGreen() * blurKernel[i + 1][j + 1];
        blueSum += newPixel.getBlue() * blurKernel[i + 1][j + 1];
      }
    }
    int newRed = clamp((int) redSum);
    int newGreen = clamp((int) greenSum);
    int newBlue = clamp((int) blueSum);

    return PixelFactory.createPixel(ImageType.RGB, newRed, newGreen, newBlue);
  }

  private int clamp(int value) {
    return Math.max(PIXEL_LOWER_LIMIT, Math.min(PIXEL_UPPER_LIMIT, value));
  }

}