package ime.model.operation;

import ime.model.image.Image;
import ime.model.image.ImageType;
import ime.model.image.SimpleImage;
import ime.model.pixel.Pixel;
import ime.model.pixel.RGBPixel;

public class AdjustLevel implements ImageOperation {

  private double coefficient_a;
  private double coefficient_b;
  private double coefficient_c;

  /**
   * Sets the coefficients for the levels adjustment based on black, mid, and white values.
   * This method should be called once before applying the operation to an image.
   */
  public void setCoefficients(int blackValue, int midValue, int whiteValue) {
    double A = Math.pow(blackValue, 2) * (midValue - whiteValue) - blackValue * (Math.pow(midValue, 2) - Math.pow(whiteValue, 2))
        + whiteValue * Math.pow(midValue, 2) - midValue * Math.pow(whiteValue, 2);
    double Aa = -blackValue * (128 - 255) + 128 * whiteValue - 255 * midValue;
    double Ab = Math.pow(blackValue, 2) * (128 - 255) + 255 * Math.pow(midValue, 2) - 128 * Math.pow(whiteValue, 2);
    double Ac = Math.pow(blackValue, 2) * (255 * midValue - 128 * whiteValue) - blackValue * (255 * Math.pow(midValue, 2) - 128 * Math.pow(whiteValue, 2));

    this.coefficient_a = Aa / A;
    this.coefficient_b = Ab / A;
    this.coefficient_c = Ac / A;
  }

  @Override
  public Image apply(Image inputImage, String... args) throws IllegalArgumentException {

    System.out.println("Applying at Model");
    //args check is at controller.
    int blackValue = Integer.parseInt(args[0]);
    int midValue = Integer.parseInt(args[1]);
    int whiteValue = Integer.parseInt(args[2]);

    setCoefficients(blackValue, midValue, whiteValue);
    System.out.println("Found the Coeffs!");

    Image outputImage = new SimpleImage(inputImage.getHeight(), inputImage.getWidth(), ImageType.RGB);
    System.out.println(outputImage.getHeight());
    System.out.println(outputImage.getWidth());

    for (int x = 0; x < inputImage.getHeight(); x++) {
      for (int y = 0; y < inputImage.getWidth(); y++) {
        int redComponent = evaluateQuadraticEquation(inputImage.getPixel(x, y).getRed());
        int greenComponent = evaluateQuadraticEquation(inputImage.getPixel(x, y).getGreen());
        int blueComponent = evaluateQuadraticEquation(inputImage.getPixel(x, y).getBlue());
        System.out.println(x+","+y);
        System.out.println(redComponent+","+greenComponent+","+blueComponent);
        outputImage.setPixel(x, y, new RGBPixel(redComponent,greenComponent,blueComponent));
      }
    }
    return outputImage;
  }

  private int evaluateQuadraticEquation(int pixelValue) {
    return (int) (coefficient_a * Math.pow(pixelValue, 2) + coefficient_b * pixelValue + coefficient_c);
  }
}