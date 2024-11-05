package ime.model.operation;

import static ime.constants.FilterConstants.PIXEL_UPPER_LIMIT;

import ime.model.pixel.Pixel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ime.model.image.Image;
import ime.model.image.ImageType;
import ime.model.image.SimpleImage;
import ime.model.pixel.RGBPixel;

public class Histogram implements ImageOperation {

  private final CountFrequency countFrequencyOperation;

  public Histogram(CountFrequency countFrequencyOperation) {
    this.countFrequencyOperation = countFrequencyOperation;
  }

  @Override
  public Image apply(Image inputImage, String... args) throws IllegalArgumentException {

    inputImage.setPixel(0,0,new RGBPixel(20,20,20));
    inputImage.setPixel(0,1,new RGBPixel(20,20,20));
    inputImage.setPixel(1,0,new RGBPixel(20,20,20));
    inputImage.setPixel(1,1,new RGBPixel(30,30,30));

    return inputImage;

//    Map<String, Map<Integer,Integer>> frequency;
//
//    frequency = countFrequencyOperation.calculateFrequencies(inputImage);
//
//    if (!frequency.containsKey("red") || frequency.get("red").isEmpty() ||
//        !frequency.containsKey("green") || frequency.get("green").isEmpty() ||
//        !frequency.containsKey("blue") || frequency.get("blue").isEmpty()) {
//      throw new IllegalStateException("Frequency maps could not be calculated.");
//    }
//
//    Image outputImage = new SimpleImage(256, 256, ImageType.RGB);
//
//    createHistogramImage(outputImage, frequency.get("red"), frequency.get("green"), frequency.get("blue"));
//
//    return outputImage;
  }



  private void createHistogramImage(
      Image outputImage,
      Map<Integer, Integer> frequencyRed,
      Map<Integer, Integer> frequencyGreen,
      Map<Integer, Integer> frequencyBlue) {

    int histogramWidth = 256;
    int histogramHeight = 256;

    fillBackground(outputImage, histogramHeight, histogramWidth);

    drawGridLines(outputImage, histogramWidth, histogramHeight);

    drawFrequencyCurves(outputImage, frequencyRed, frequencyGreen, frequencyBlue);
  }

  private void fillBackground(Image histogramImage, int height, int width) {
    for (int x = 0; x < height; x++) {
      for (int y = 0; y < width; y++) {
        histogramImage.setPixel(x, y, new RGBPixel(255, 255, 255)); // White color
      }
    }
  }

  private void drawGridLines(Image histogramImage, int width, int height) {
    for (int j = 0; j < width; j += 16) {
      drawLine(histogramImage, 0, j, width - 1, j, new RGBPixel(200, 200, 200));
    }

    for (int i = 0; i < height; i += 16) {
      drawLine(histogramImage, i, 0, i, height - 1, new RGBPixel(200, 200, 200));
    }
  }

  private int findMaxFrequency(List<Map<Integer, Integer>> frequencyMaps) {
    int maxFrequency = 0;
    for (Map<Integer, Integer> frequencyMap : frequencyMaps) {
      maxFrequency =
          Math.max(maxFrequency, frequencyMap.values().stream().max(Integer::compare).orElse(1));
    }
    return maxFrequency;
  }

  private void drawFrequencyCurves(Image histogramImage, Map<Integer, Integer> frequencyRed,
      Map<Integer, Integer> frequencyGreen,
      Map<Integer, Integer> frequencyBlue) {

    int maxFrequency = findMaxFrequency(Arrays.asList(frequencyRed, frequencyGreen, frequencyBlue));

    for (int i = 0; i <= 255; i++) {
      drawCurveSegment(histogramImage, i, frequencyRed, maxFrequency, new RGBPixel(255, 0, 0));
      drawCurveSegment(histogramImage, i, frequencyGreen, maxFrequency, new RGBPixel(0, 255, 0));
      drawCurveSegment(histogramImage, i, frequencyBlue, maxFrequency, new RGBPixel(0, 0, 255));
    }
  }

  private void drawCurveSegment(Image histogramImage, int pixelValue,
      Map<Integer, Integer> frequencyMap,
      int maxFrequency,
      Pixel pixel) {

    int currentFrequency =
        histogramImage.getHeight() - (int) (
            (double) frequencyMap.getOrDefault(pixelValue, 0) / maxFrequency
                * histogramImage.getHeight());
    int nextFrequency = histogramImage.getHeight() - (int) (
        (double) frequencyMap.getOrDefault(pixelValue + 1, 0) / maxFrequency
            * histogramImage.getHeight());

    if (pixelValue < PIXEL_UPPER_LIMIT
        && currentFrequency >= 0
        && currentFrequency < histogramImage.getWidth()
        && nextFrequency >= 0
        && nextFrequency < histogramImage.getWidth()) {

      drawLine(histogramImage, pixelValue, currentFrequency,
          pixelValue + 1, nextFrequency, pixel);
    }
  }

  /**
   * Implements the Bresenham's algorithm as described here.
   * <a href="https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm">Link to Wiki</a>
   * Variable naming are done as per the common convention used in the algorithm derivation.
   */
  private void drawLine(Image image, int x1, int y1, int x2, int y2, Pixel pixel) {
    int dx = Math.abs(x2 - x1);
    int dy = Math.abs(y2 - y1);
    int sx = x1 < x2 ? 1 : -1;
    int sy = y1 < y2 ? 1 : -1;
    int D = dx - dy;

    while (true) {

      if (x1 >= 0 && x1 < image.getWidth() && y1 >= 0 && y1 < image.getHeight()) {
        image.setPixel(y1, x1, pixel);
      }

      if (x1 == x2 && y1 == y2) {
        break;
      }

      int updatedD = 2 * D;
      if (updatedD > -dy) {
        /*This Condition would mean that the difference in Y direction is minimal
        * and that we should move towards X.
        * updatedD is proportional to D which is in turn proportional to dx.
        *  */
        D -= dy;
        x1 += sx;
      }
      if (updatedD < dx) {
        D += dx;
        y1 += sy;
      }
    }
  }
}
