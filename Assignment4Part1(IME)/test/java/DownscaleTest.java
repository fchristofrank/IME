import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Objects;

import ime.controller.cli.ImageProcessorCLI;
import ime.controller.cli.OperationCreator;
import ime.controller.imageio.ImageFormat;
import ime.controller.imageio.ImageReader;
import ime.controller.imageio.ImageReaderFactory;
import ime.controller.operation.CLIOperation;
import ime.controller.operation.ImageOperationFactory;
import ime.model.image.Image;
import ime.model.image.ImageType;
import ime.model.image.SimpleImage;
import ime.model.operation.Downscale;
import ime.model.operation.ImageOperation;
import ime.model.operation.Sharpen;

import static org.junit.Assert.assertEquals;

public class DownscaleTest {

  private void runTest(String commandScript) {
    Readable readableInput = new StringReader(commandScript);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OperationCreator operationCreator = new ImageOperationFactory();
    new ImageProcessorCLI(readableInput, new PrintStream(outputStream), operationCreator).run();
  }

  @Test
  public void testDownscale() {

    String resDirPath =
        Objects.requireNonNull(getClass().getClassLoader().getResource("")).getPath();
    ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.JPG);
    Image image;
    try {
      image = imageReader.read(resDirPath + "boston-sharpen-wp-actual.jpg", ImageType.RGB);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }
    ImageOperation operation = new Downscale();
    operation.apply(image, "test", "test", "100", "100");
  }

  @Test
  public void testMaskOperation() {

    String path = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getPath();
    String commandScript = "load "
            + path
            + "testImageMasking.png inputImageName\n"
            + "load "
            + path
            + "maskImage.png maskImageName\n"
            + "brighten 50 inputImageName maskImageName brightened\n"
            + "save "
            + path
            + "brightened.png brightened\n"
            + "darken 50 inputImageName maskImageName darkened\n"
            + "save "
            + path
            + "darkened.png darkened\n"
            + "red-component inputImageName maskImageName red\n"
            + "save "
            + path
            + "redMask.png red\n"
            + "green-component inputImageName maskImageName green\n"
            + "save "
            + path
            + "greenMask.png green\n"
            + "blue-component inputImageName maskImageName blue\n"
            + "save "
            + path
            + "blueMask.png blue\n"
            + "luma-component inputImageName maskImageName luma\n"
            + "save "
            + path
            + "lumaMask.png luma\n"
            + "intensity-component inputImageName maskImageName luma\n"
            + "save "
            + path
            + "intensityMask.png luma\n"
            + "value-component inputImageName maskImageName luma\n"
            + "save "
            + path
            + "valueMask.png luma\n"
            + "blur inputImageName maskImageName blur\n"
            + "save "
            + path
            + "blurMask.png blur\n"
            + "sharpen inputImageName maskImageName sharpened\n"
            + "save "
            + path
            + "sharpened.png sharpened\n"
            + "sepia inputImageName maskImageName sepia\n"
            + "save "
            + path
            + "sepiaMask.png sepia\n"
            + "exit";

    runTest(commandScript);
    Image actualImage;

    try {
      // 1. Brighten Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "brightened.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 3289855};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

    try {
      // 2. Darken Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "darkened.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 205};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

    try {
      // 3. Red Component Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "redMask.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 0};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

    try {
      // 4. Green Component Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "greenMask.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 0};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

    try {
      // 5. Blue Component Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "blueMask.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 16777215};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

    try {
      // 6. Luma Component Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "lumaMask.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 1184274};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

    try {
      // 7. Value Component Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "valueMask.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 16777215};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

    try {
      // 8. Intensity Component Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "intensityMask.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 5592405};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

    try {
      // 9. Blur Component Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "blurMask.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 3096383};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

    try {
      // 10. Sharpen Component Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "sharpened.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 8355839};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

    try {
      // 11. Sepia Component Test
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "sepiaMask.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 3156769};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }

  }


  @Test (expected = IllegalArgumentException.class)
  public void testMaskInvalidDimensionsOperation() {

    //Loads both Image of Invalid Dimensions.
    String path = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getPath();
    String commandScript = "load "
            + path
            + "testImageMasking.png inputImageName\n"
            + "load "
            + path
            + "maskImage.png maskImageName\nexit";
    runTest(commandScript);
    OperationCreator operator = new ImageOperationFactory();
    CLIOperation sharpen = operator.createOperation("sharpen");
    //Throws error due to invalid dimensions in images.
    sharpen.execute("inputImageName","maskImageName","output");
  }

  @Test
  public void testMaskOperationWithFullMask() {

    String path = Objects.requireNonNull(getClass().getClassLoader().getResource("")).getPath();
    String commandScript = "load "
            + path
            + "testImageMasking.png inputImageName\n"
            + "load "
            + path
            + "maskImage.png maskImageName\n"
            + "brighten 50 inputImageName maskImageName brightened\n"
            + "save "
            + path
            + "brightened.png brightened\n"
            + "darken 50 inputImageName maskImageName darkened\n"
            + "save "
            + path
            + "darkened.png darkened\n"
            + "red-component inputImageName maskImageName red\n"
            + "save "
            + path
            + "redMask.png red\n"
            + "green-component inputImageName maskImageName green\n"
            + "save "
            + path
            + "greenMask.png green\n"
            + "blue-component inputImageName maskImageName blue\n"
            + "save "
            + path
            + "blueMask.png blue\n"
            + "luma-component inputImageName maskImageName luma\n"
            + "save "
            + path
            + "lumaMask.png luma\n"
            + "intensity-component inputImageName maskImageName luma\n"
            + "save "
            + path
            + "intensityMask.png luma\n"
            + "value-component inputImageName maskImageName luma\n"
            + "save "
            + path
            + "valueMask.png luma\n"
            + "blur inputImageName maskImageName blur\n"
            + "save "
            + path
            + "blurMask.png blur\n"
            + "sharpen inputImageName maskImageName sharpened\n"
            + "save "
            + path
            + "sharpened.png sharpened\n"
            + "sepia inputImageName maskImageName sepia\n"
            + "save "
            + path
            + "sepiaMask.png sepia\n"
            + "exit";

    runTest(commandScript);
    Image actualImage;

    try {
      ImageReader imageReader = ImageReaderFactory.createReader(ImageFormat.PNG);
      actualImage = imageReader.read(path + "brightened.png", ImageType.RGB);
      int[] expectedPixelValue = new int[] {16711680, 65280, 16776960, 3289855};

      assertEquals(expectedPixelValue[0], actualImage.getPixel(0, 0).getColorComponents());
      assertEquals(expectedPixelValue[1], actualImage.getPixel(0, 1).getColorComponents());
      assertEquals(expectedPixelValue[2], actualImage.getPixel(1, 0).getColorComponents());
      assertEquals(expectedPixelValue[3], actualImage.getPixel(1, 1).getColorComponents());
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to read image file", e);
    }
  }

}
