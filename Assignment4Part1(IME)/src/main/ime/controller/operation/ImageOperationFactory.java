package ime.controller.operation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import ime.controller.cli.OperationCreator;
import ime.controller.imageio.ImageFormat;
import ime.controller.imageio.ImageReader;
import ime.controller.imageio.ImageReaderFactory;
import ime.controller.imageio.ImageWriter;
import ime.controller.imageio.ImageWriterFactory;
import ime.model.image.Image;
import ime.model.image.ImageType;
import ime.model.operation.AbstractVisualize;
import ime.model.operation.ApplyBrightness;
import ime.model.operation.ApplyCompression;
import ime.model.operation.ApplyHorizontalFlip;
import ime.model.operation.ApplySepia;
import ime.model.operation.ApplySepiaWithPreview;
import ime.model.operation.ApplyVerticalFlip;
import ime.model.operation.Blur;
import ime.model.operation.Combine;
import ime.model.operation.CountFrequency;
import ime.model.operation.Downscale;
import ime.model.operation.ImageOperation;
import ime.model.operation.MaskOperation;
import ime.model.operation.Sharpen;
import ime.model.operation.VisualizeBlue;
import ime.model.operation.VisualizeGreen;
import ime.model.operation.VisualizeIntensity;
import ime.model.operation.VisualizeLuma;
import ime.model.operation.VisualizeRed;
import ime.model.operation.VisualizeValue;
import ime.model.pixel.Pixel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class for creating CLI operations in an image processing application. This class creates CLI
 * operations to corresponding operation specified in the command.
 */
public class ImageOperationFactory implements OperationCreator {

  protected final ImageRepo imageLibrary;

  /**
   * Constructs a CommandFactory with the specified image library.
   */
  public ImageOperationFactory() {
    this.imageLibrary = new ImageLibrary();
  }

  /**
   * Creates and returns a CLIOperation based on the given command name.
   *
   * <p>This method supports the following commands:
   *
   * <ul>
   *   <li>load
   *   <li>save
   *   <li>rgb-split
   *   <li>brighten
   *   <li>darken
   *   <li>vertical-flip
   *   <li>horizontal-flip
   *   <li>sepia
   *   <li>rgb-combine
   *   <li>blur
   *   <li>sharpen
   *   <li>component (various component visualizations)
   * </ul>
   *
   * @param commandName the name of the operation to create
   * @return a CLIOperation instance corresponding to the given command name
   * @throws IllegalArgumentException if the command name is not recognized
   */
  @Override
  public CLIOperation createOperation(String commandName) {
    switch (commandName.toLowerCase()) {
      case Commands.LOAD:
        return new Load(imageLibrary);
      case Commands.SAVE:
        return new Save(imageLibrary);
      case Commands.RGB_SPLIT:
        return new RGBSplit(imageLibrary);
      case Commands.BRIGHTEN:
        return new BrightenWithMask(imageLibrary);
      case Commands.DARKEN:
        return new DarkenWithMask(imageLibrary);
      case Commands.VERTICAL_FLIP:
        return new VerticalFlip(imageLibrary);
      case Commands.HORIZONTAL_FLIP:
        return new HorizontalFlip(imageLibrary);
      case Commands.RGB_COMBINE:
        return new CombineRGB(imageLibrary);
      // Filter commands
      case Commands.BLUR:
      case Commands.SHARPEN:
      case Commands.SEPIA:
        return new FilterWithMask(imageLibrary, commandName);
      // Visualize commands
      case Commands.RED_COMPONENT:
      case Commands.GREEN_COMPONENT:
      case Commands.BLUE_COMPONENT:
      case Commands.LUMA_COMPONENT:
      case Commands.VALUE_COMPONENT:
      case Commands.INTENSITY_COMPONENT:
        return new VisualizeWithMask(imageLibrary, commandName);
      case Commands.COMPRESS:
        return new Compress(imageLibrary);
      case Commands.HISTOGRAM:
        return new Histogram(imageLibrary);
      case Commands.COLOR_CORRECTION:
        return new ColorCorrection(imageLibrary);
      case Commands.LEVELS_ADJUST:
        return new AdjustLevel(imageLibrary);
      case Commands.DOWNSCALE:
        return new DownScale(imageLibrary);

      default:
        throw new IllegalArgumentException("Unknown command: " + commandName);
    }
  }

  /**
   * This method converts image to buffered image representation.
   *
   * @param image the image which has to be converted to buffered image representation.
   * @return the converted image.
   */
  protected BufferedImage convertToBufferedImage(Image image) {
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

  /**
   * Contains command names for CLI operations as constants.
   */
  protected static class Commands {

    public static final String LOAD = "load";
    public static final String SAVE = "save";
    public static final String RGB_SPLIT = "rgb-split";
    public static final String BRIGHTEN = "brighten";
    public static final String DARKEN = "darken";
    public static final String VERTICAL_FLIP = "vertical-flip";
    public static final String HORIZONTAL_FLIP = "horizontal-flip";
    public static final String SEPIA = "sepia";
    public static final String RGB_COMBINE = "rgb-combine";
    public static final String BLUR = "blur";
    public static final String SHARPEN = "sharpen";
    public static final String RED_COMPONENT = "red-component";
    public static final String GREEN_COMPONENT = "green-component";
    public static final String BLUE_COMPONENT = "blue-component";
    public static final String LUMA_COMPONENT = "luma-component";
    public static final String VALUE_COMPONENT = "value-component";
    public static final String INTENSITY_COMPONENT = "intensity-component";
    public static final String HISTOGRAM = "histogram";
    public static final String COMPRESS = "compress";
    public static final String COLOR_CORRECTION = "color-correct";
    public static final String LEVELS_ADJUST = "levels-adjust";
    public static final String DOWNSCALE = "downscale";
    public static final String CLEAR_LIBRARY = "clear-library";
  }

  /**
   * Controller class for loading images into the image library. This class is responsible for
   * reading an image file from the specified path and adding it to the image library under a given
   * name.
   */
  class Load extends AbstractOperation {

    private final Logger LOGGER = Logger.getLogger(Load.class.getName());

    /**
     * Constructs a Load operation controller with the specified image library.
     *
     * @param library the ImageLibrary instance that provides access to the images.
     */
    public Load(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      String imagePath = args[0];
      imagePath = imagePath.replace("\"", "");
      String imageName = args[1];
      String[] parts = imagePath.split("\\.");
      String imageFormat = parts[parts.length - 1];
      ImageReader imageReader = null;
      try {
        imageReader =
            ImageReaderFactory.createReader(ImageFormat.valueOf(imageFormat.toUpperCase()));
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid image format: " + imageFormat);
      }
      Image image;
      try {
        image = imageReader.read(imagePath, ImageType.RGB);
      } catch (NullPointerException | IOException e) {
        throw new IllegalArgumentException(
            "Error reading image file: "
                + imagePath
                + ". Please ensure the file exists and is a valid image format.",
            e);
      } catch (Exception e) {
        throw new IllegalArgumentException("There is an internal error");
      }
      addImage(imageName, image);
      System.out.println("Image loaded :: " + imageName);
    }
  }

  /**
   * Controller class for saving images from the image library to the file system. This class is
   * responsible for converting an image to a buffered image and writing it to the specified file
   * path in the desired format.
   */
  class Save extends AbstractOperation {

    /**
     * Constructs a Save operation controller with the specified image library.
     *
     * @param library the ImageLibrary instance that provides access to the images.
     */
    public Save(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      String imagePath = args[0];
      imagePath = imagePath.replace("\"", "");
      try {
        String[] parts = imagePath.split("\\.");
        ImageFormat imageFormat = ImageFormat.valueOf(parts[parts.length - 1].toUpperCase());
        String imageName = args[1];
        Image simpleImage = getImage(imageName);
        BufferedImage bufferedImage = convertToBufferedImage(simpleImage);

        ImageWriter writer = ImageWriterFactory.createWriter(imageFormat);
        writer.writeImage(bufferedImage, imagePath);
        System.out.println("Saved :: " + imageName + " in " + imagePath);
      } catch (IllegalArgumentException | IOException e) {
        throw new IllegalArgumentException("Unable to save the file: " + imagePath, e);
      } catch (Exception e) {
        throw new IllegalArgumentException("There is an internal error");
      }
    }
  }

  /**
   * Controller class for performing RGB separation operations on images. This class is responsible
   * for splitting an input image into its red, green, and blue components, creating separate images
   * for each color.
   */
  class RGBSplit extends AbstractOperation {

    public RGBSplit(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      String inputImageName = args[0];
      String redOutputImageName = args[1];
      String greenOutputImageName = args[2];
      String blueOutputImageName = args[3];
      Image inputImage = getImage(inputImageName);
      if (inputImage == null) {
        throw new IllegalArgumentException("Input image not found");
      }
      Image redImage = inputImage.applyOperation(new VisualizeRed());
      Image greenImage = inputImage.applyOperation(new VisualizeGreen());
      Image blueImage = inputImage.applyOperation(new VisualizeBlue());
      addImage(redOutputImageName, redImage);
      addImage(greenOutputImageName, greenImage);
      addImage(blueOutputImageName, blueImage);
      System.out.println(
          "The images have been separated into their red, blue, and green components "
              + "and combined accordingly.");
    }

    @Override
    protected void validateArgs(String[] args) {
      if (args.length != 4) {
        throw new IllegalArgumentException("Invalid number of arguments");
      }
    }
  }

  /**
   * Abstract controller class for adjusting image brightness operations. This class is responsible
   * for validating the input arguments and routing them to the appropriate operation for
   * execution.
   */
  abstract class AdjustBrightness extends AbstractOperation {

    /**
     * This method creates an operation to adjust brightness.
     *
     * @param library the ImageLibrary to be used for image operations.
     */
    public AdjustBrightness(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      int alpha = Integer.parseInt(args[0]);
      String inputName = args[1];
      String outputName = args[2];
      Image inputImage = getImage(inputName);
      if (inputImage == null) {
        throw new IllegalArgumentException("Input image not found");
      }
      Image outputImage = inputImage.applyOperation(new ApplyBrightness(), String.valueOf(alpha));
      addImage(outputName, outputImage);
      System.out.println("Adjusting Brightness. New image created :: " + outputName);
    }

    @Override
    protected void validateArgs(String[] args) throws IllegalArgumentException {
      if (args.length != 3) {
        throw new IllegalArgumentException("Invalid number of arguments");
      }
      try {
        Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            String.format(
                "Invalid brightness adjustment value '%s'." + " It must be an integer.", args[0]));
      }
    }
  }

  /**
   * Controller class for performing image brightening operations. This class is responsible for
   * validating input arguments and routing them to the appropriate brightening operation for
   * execution.
   */
  class Brighten extends AdjustBrightness {

    /**
     * This method creates an operation to brighten the image.
     *
     * @param library the ImageLibrary to be used for image operations.
     */
    public Brighten(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      super.execute(args);
    }
  }

  /**
   * Controller class for performing image darkening operations. This class is responsible for
   * validating input arguments and routing them to the appropriate darkening operation for
   * execution.
   */
  class Darken extends AdjustBrightness {

    /**
     * This method creates an operation to darken the image.
     *
     * @param library the ImageLibrary to be used for image operations.
     */
    public Darken(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      int darkenValue = Integer.parseInt(args[0]);
      args[0] = String.valueOf(-Math.abs(darkenValue));
      super.execute(args);
    }
  }

  class AdjustLevel extends AbstractOperation {

    /**
     * Constructs an AbstractOperation with the specified image library.
     *
     * @param library the ImageLibrary to be used for image operations
     */
    public AdjustLevel(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      String inputName = args[3];
      String outputName = args[4];
      Image inputImage = getImage(inputName);
      if (inputImage == null) {
        throw new IllegalArgumentException("Input image not found");
      }
      Image outputImage = inputImage.applyOperation(new ime.model.operation.AdjustLevel(), args);
      addImage(outputName, outputImage);
      System.out.println("Adjusted the Level. New Image :: " + outputName);
    }

    @Override
    protected void validateArgs(String[] args) throws IllegalArgumentException {

      if (args.length == 7) {
        String splitCommand = args[5];
        if (!splitCommand.equals("split")) {
          throw new IllegalArgumentException("Invalid split command: " + splitCommand);
        }
        String splitPercentage = args[6];
        try {
          int percentage = Integer.parseInt(splitPercentage);
          if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException(
                    "Percentage value for split line must be between " + "0 and 100.");
          }
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Percentage value for split line must be a number.");
        }
      }

      if (args.length < 5) {
        throw new IllegalArgumentException("Invalid number of arguments");
      }

      try {
        int arg0 = Integer.parseInt(args[0]);
        int arg1 = Integer.parseInt(args[1]);
        int arg2 = Integer.parseInt(args[2]);

        if (arg0 > arg1 || arg1 > arg2) {
          throw new IllegalArgumentException("Values should be in ascending order");
        }

        if (arg0 < 0 || arg0 > 255 || arg1 > 255 || arg2 > 255) {
          throw new IllegalArgumentException("Values should be between 0 to 255 only.");
        }
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Black, middle, and white values should be " +
                "valid integers");
      }
    }
  }

  class ColorCorrection extends AbstractOperation {

    public ColorCorrection(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      String inputName = args[0];
      String outputName = args[1];

      Image inputImage = getImage(inputName);
      if (inputImage == null) {
        throw new IllegalArgumentException("Input image not found");
      }
      Image outputImage =
              inputImage.applyOperation(
                      new ime.model.operation.ColorCorrection(new CountFrequency()), args);
      addImage(outputName, outputImage);
      System.out.println("Generated Color Corrected Image. New Image :: " + outputName);
    }

    @Override
    protected void validateArgs(String[] args) throws IllegalArgumentException {

      if (args.length > 4) {
        throw new IllegalArgumentException("Not Supported number of arguements");
      }

      if (args.length == 4) {
        String splitCommand = args[2];
        if (!splitCommand.equals("split")) {
          throw new IllegalArgumentException("Invalid split command: " + splitCommand);
        }
        String splitPercentage = args[3];
        try {
          int percentage = Integer.parseInt(splitPercentage);
          if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException(
                    "Percentage value for split line must be between " + "0 and 100.");
          }
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Percentage value for split line must be a number.");
        }
      }

      if (args.length == 4 && (Integer.parseInt(args[3]) < 0 && Integer.parseInt(args[3]) > 100)) {
        throw new IllegalArgumentException("Preview Width should be between 0 to 100");
      }
    }
  }

  /**
   * The given Filter controller validates the args received from the command line, and it gives the
   * object of the right instance based on blur or sharpen object with which the actual filter
   * operation can be implemented. It also is responsible in maintaining the output of the images
   * inside the library object to maintain the historic operations and objects/
   */
  class Filter extends OperationWithPreview {

    private final String command;

    public Filter(ImageRepo library, String command) {
      super(library);
      this.command = command;
    }

    /**
     * Command line argument verification takes place after which the images on which the user
     * intends to perform the operation is loaded from the library object and the request is routed
     * to the correct and desired filter operations.
     *
     * @param args the arguments for an operations.
     * @throws IllegalArgumentException if the user tries to operate on image that is not loaded
     *                                  yet.
     */
    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      String inputName = args[0];
      String outputName = args[1];
      Image inputImage = getImage(inputName);
      if (inputImage == null) {
        throw new IllegalArgumentException("Input image not found");
      }
      String[] commandArgs = Arrays.copyOfRange(args, 2, args.length);
      Image outputImage = inputImage.applyOperation(filterObjectFactory(this.command), commandArgs);
      addImage(outputName, outputImage);
      System.out.println(this.command+" applied to given image. New Image :: " + outputName);
    }

    /**
     * Responsible for returning the object as per the operation specified.
     *
     * @param command the intended operation is sent as argument for the method.
     * @return returns the object based on the command received.
     */
    protected ImageOperation filterObjectFactory(String command) {

      switch (command) {
        case Commands.BLUR:
          return new Blur();
        case Commands.SHARPEN:
          return new Sharpen();
        case Commands.SEPIA:
          return new ApplySepia();
        default:
          throw new UnsupportedOperationException("Unknown command given.");
      }
    }
  }

  /**
   * This class represents an operation with preview.
   */
  abstract class OperationWithPreview extends AbstractOperation {

    /**
     * Constructs an AbstractOperation with the specified image library.
     *
     * @param library the ImageLibrary to be used for image operations
     */
    public OperationWithPreview(ImageRepo library) {
      super(library);
    }

    @Override
    protected void validateArgs(String[] args) throws IllegalArgumentException {
      if (args.length != 2 && args.length != 4) {
        throw new IllegalArgumentException("Invalid number of arguments");
      }

      if (args.length == 4) {
        String splitCommand = args[2];
        if (!splitCommand.equals("split")) {
          throw new IllegalArgumentException("Invalid split command: " + splitCommand);
        }
        String splitPercentage = args[3];
        try {
          int percentage = Integer.parseInt(splitPercentage);
          if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException(
                    "Percentage value for split line must be between " + "0 and 100.");
          }
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Percentage value for split line must be a number.");
        }
      }
    }
  }

  /**
   * This class represents filter operations with preview.
   */
  class FilterWithPreview extends Filter {

    public FilterWithPreview(ImageRepo library, String command) {
      super(library, command);
    }

    @Override
    protected ImageOperation filterObjectFactory(String command) {
      ImageOperation imageOperation = super.filterObjectFactory(command);
      if (command.equals("sepia")) {
        imageOperation = new ApplySepiaWithPreview();
      }
      return imageOperation;
    }
  }

  /**
   * Controller for combining three images into one using their RGB channels. The first image
   * supplies the red channel, the second the green, and the third the blue. The operation is
   * strictly ordered and requires exactly three images.
   */
  class CombineRGB extends AbstractOperation {

    /**
     * Constructs a CombineRGB operation that will use the provided image library for its
     * input/output.
     *
     * @param library the library containing images available for operations.
     */
    public CombineRGB(ImageRepo library) {
      super(library);
    }

    /**
     * Executes the combine operation by extracting red, green, and blue channels from three images
     * and merging them into one image. The first argument specifies the name of the output image.
     * The next three arguments specify the names of the images used for red, green, and blue
     * channels.
     *
     * @param args an array containing the output image name and three input image names.
     * @throws IllegalArgumentException if exactly three input images are not provided or any image
     *                                  is missing.
     */
    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      String inputName = args[0];
      Image redImage = getImage(args[1]);
      Image greenImage = getImage(args[2]);
      Image blueImage = getImage(args[3]);
      if (redImage == null || greenImage == null || blueImage == null) {
        throw new IllegalArgumentException("Input image not found");
      }
      Image outputImage =
              redImage.applyOperation(
                      new Combine(), Arrays.asList(redImage, greenImage, blueImage), args);
      addImage(inputName, outputImage);
      System.out.println("Combine given images. New Image :: " + inputName);
    }

    @Override
    protected void validateArgs(String[] args) {
      if (args.length != 4) {
        throw new IllegalArgumentException("Invalid number of arguments");
      }
    }
  }

  /**
   * Abstract controller class for compressing image brightness operations. This class is
   * responsible for validating the input arguments and routing them to the appropriate operation
   * for execution.
   */
  class Compress extends AbstractOperation {

    /**
     * Constructs an AbstractOperation with the specified image library.
     *
     * @param library the ImageLibrary to be used for image operations
     */
    public Compress(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      String inputImageName = args[1];
      String outputImageName = args[2];
      Image inputImage = getImage(inputImageName);
      Image outputImage = inputImage.applyOperation(new ApplyCompression(), args[0]);
      System.out.println(
              "Applied compression to :: "
                      + inputImageName
                      + ". New image created :: "
                      + outputImageName);
      addImage(outputImageName, outputImage);
    }

    @Override
    protected void validateArgs(String[] args) throws IllegalArgumentException {
      if (args.length != 3) {
        throw new IllegalArgumentException("Invalid number of arguments");
      }
    }
  }

  /**
   * Abstract controller class for performing image flipping operations. This class is responsible
   * for validating the input arguments and routing them to the appropriate operation for
   * execution.
   */
  abstract class Flip extends AbstractOperation {

    /**
     * This method creates an operation to flip the image.
     *
     * @param library the ImageLibrary to be used for image operations.
     */
    public Flip(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      String inputName = args[0];
      String outputName = args[1];
      Image inputImage = getImage(inputName);
      if (inputImage == null) {
        throw new IllegalArgumentException("Input image not found");
      }
      Image outputImage = inputImage.applyOperation(getOperation());
      addImage(outputName, outputImage);
      System.out.println("Applying " + getOperationName() + ". New image created: " + outputName);
    }

    /**
     * This method gets the corresponding operation.
     *
     * @return the specific ImageOperation instance.
     */
    protected abstract ImageOperation getOperation();

    /**
     * This method gets the specific operation name.
     *
     * @return the name of the operation.
     */
    protected abstract String getOperationName();
  }

  /**
   * Controller class for executing horizontal image flipping operations. This class is responsible
   * for validating input arguments specific to the horizontal flip operation and routing them to
   * the appropriate image operation for execution.
   */
  class HorizontalFlip extends Flip {

    /**
     * This method creates an operation to flip the image horizontally.
     *
     * @param library the ImageLibrary to be used for image operations.
     */
    public HorizontalFlip(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      super.execute(args);
    }

    @Override
    protected ImageOperation getOperation() {
      return new ApplyHorizontalFlip();
    }

    @Override
    protected String getOperationName() {
      return "Horizontal Flip";
    }
  }

  /**
   * Controller class for executing vertical image flipping operations. This class is responsible
   * for validating input arguments specific to the vertical flip operation and routing them to the
   * appropriate image operation for execution.
   */
  class VerticalFlip extends Flip {

    /**
     * This method creates an operation to flip the image vertically.
     *
     * @param library the ImageLibrary to be used for image operations.
     */
    public VerticalFlip(ImageRepo library) {
      super(library);
    }

    @Override
    public void execute(String... args) throws IllegalArgumentException {
      super.execute(args);
    }

    @Override
    protected ImageOperation getOperation() {
      return new ApplyVerticalFlip();
    }

    @Override
    protected String getOperationName() {
      return "Vertical Flip";
    }
  }

  /**
   * The controller class for the visualize operation. This class is responsible to validate the
   * visualize arguments and then route to the intended operation.
   */
  class Visualize extends OperationWithPreview {

    private final String command;

    public Visualize(ImageRepo library, String command) {
      super(library);
      this.command = command;
    }

    /**
     * Responsible to interface between CLI and operation model. Performs the validation of
     * arguments and adds image back to library that holds the list of image of processed images by
     * the user.
     *
     * @param args the arguments for an operations.
     * @throws IllegalArgumentException the input image must be valid.
     */
    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      String inputName = args[0];
      String outputName = args[1];
      Image inputImage = getImage(inputName);
      if (inputImage == null) {
        throw new IllegalArgumentException("Input image not found");
      }
      String[] commandArgs = Arrays.copyOfRange(args, 2, args.length);
      Image outputImage =
              inputImage.applyOperation(visualizeObjectFactory(this.command), commandArgs);
      addImage(outputName, outputImage);
      System.out.println("Extracted given component. New Image :: " + outputName);
    }

    private AbstractVisualize visualizeObjectFactory(String command) {
      switch (command) {
        case Commands.RED_COMPONENT:
          return new VisualizeRed();
        case Commands.GREEN_COMPONENT:
          return new VisualizeGreen();
        case Commands.BLUE_COMPONENT:
          return new VisualizeBlue();
        case Commands.VALUE_COMPONENT:
          return new VisualizeValue();
        case Commands.LUMA_COMPONENT:
          return new VisualizeLuma();
        case Commands.INTENSITY_COMPONENT:
          return new VisualizeIntensity();
        default:
          throw new IllegalArgumentException("Unknown component: " + command);
      }
    }
  }

  /**
   * This class represents visualize operations with preview.
   */
  class VisualizeWithPreview extends Visualize {

    public VisualizeWithPreview(ImageRepo library, String command) {
      super(library, command);
    }
  }

  class DownScale extends AbstractOperation {

    /**
     * Downscales the given image with the averaging algorithm
     */
    public DownScale(ImageRepo library) {
      super(library);
    }

    /**
     * This method executes a specific operation with the given arguments.
     *
     * @param args the arguments for an operations.
     * @throws IllegalArgumentException if the operation cannot be performed due to invalid or
     *                                  insufficient arguments.
     */
    @Override
    public void execute(String... args) throws IllegalArgumentException {

      validateArgs(args);
      String inputName = args[0];
      String outputName = args[1];
      String width = args[2];
      String height = args[3];

      Image inputImage = getImage(inputName);
      if (inputImage == null) {
        throw new IllegalArgumentException("Input image not found");
      }
      Image outputImage = inputImage.applyOperation(new Downscale(), width, height);
      addImage(outputName, outputImage);
      System.out.println("Generated DownScaled Image. New Image :: " + outputName);
    }

    @Override
    public void validateArgs(String[] args) {

      if (args.length < 4) {
        throw new IllegalArgumentException("Downscaled Height and Width are required.");
      }

      if (!isNumeric(args[2]) || !isNumeric(args[3])) {
        throw new IllegalArgumentException("Cannot downsize to non-numeric value.");
      }
    }

    /**
     * Helper method to check if a string represents a numeric value.
     *
     * @param str the string to check
     * @return true if the string is numeric, false otherwise
     */
    private boolean isNumeric(String str) {
      try {
        Integer.parseInt(str);
        return true;
      } catch (Exception e) {
        System.out.println(str);
        return false;
      }
    }
  }

  /**
   * Represents a histogram generation operation for an image.
   * This operation computes and visualizes the frequency distribution of pixel values.
   */
  class Histogram extends AbstractOperation {

    /**
     * Constructs a Histogram operation with the specified image library.
     *
     * @param library the ImageRepo to be used for accessing images
     */
    public Histogram(ImageRepo library) {
      super(library);
    }

    /**
     * Executes the histogram generation operation.
     *
     * @param args the arguments containing the input image name and the output image name
     * @throws IllegalArgumentException if the input image is not found or arguments are invalid
     */
    @Override
    public void execute(String... args) throws IllegalArgumentException {
      validateArgs(args);
      String inputName = args[0];
      String outputName = args[1];
      Image inputImage = getImage(inputName);
      if (inputImage == null) {
        throw new IllegalArgumentException("Input image not found");
      }
      Image outputImage = inputImage.applyOperation(
          new ime.model.operation.Histogram(new CountFrequency()), args);
      addImage(outputName, outputImage);
      System.out.println("Generated Histogram. New Image :: " + outputName);
    }
  }

  /**
   * Applies a filter operation to an image using a mask to selectively modify parts of the image.
   */
  public class FilterWithMask extends FilterWithPreview {

    /**
     * Constructs a FilterWithMask operation.
     *
     * @param library  the ImageRepo to be used for accessing images
     * @param command  the filter command to apply
     */
    public FilterWithMask(ImageRepo library, String command) {
      super(library, command);
    }

    /**
     * Executes the filter operation with an optional mask image.
     *
     * @param args the arguments containing input image, mask image, and output image names
     * @throws IllegalArgumentException if dimensions do not match or arguments are invalid
     */
    @Override
    public void execute(String... args) throws IllegalArgumentException {
      Image inputImage = getImage(args[0]);
      String maskImageName = args[1];

      if (args.length > 2 && imageLibrary.getImage(maskImageName) != null) {
        Image maskImage = getImage(maskImageName);
        validateDimensions(inputImage, maskImage);

        String inputImageName = args[0];
        String outputImageName = args[2];
        super.execute(inputImageName, outputImageName);

        Image outputImage = inputImage.applyOperation(
            new MaskOperation(new VisualizeLuma()),
            Arrays.asList(inputImage, maskImage, imageLibrary.getImage(outputImageName))
        );

        addImage(outputImageName, outputImage);
      } else {
        super.execute(args);
      }
    }
  }

  /**
   * Visualizes an image with selective areas modified based on a mask.
   */
  public class VisualizeWithMask extends VisualizeWithPreview {

    /**
     * Constructs a VisualizeWithMask operation.
     *
     * @param library  the ImageRepo to be used for accessing images
     * @param command  the visualization command to apply
     */
    public VisualizeWithMask(ImageRepo library, String command) {
      super(library, command);
    }

    /**
     * Executes the visualization operation with an optional mask image.
     *
     * @param args the arguments containing input image, mask image, and output image names
     * @throws IllegalArgumentException if dimensions do not match or arguments are invalid
     */
    @Override
    public void execute(String... args) throws IllegalArgumentException {
      Image inputImage = getImage(args[0]);
      String maskImageName = args[1];

      if (args.length > 2 && imageLibrary.getImage(maskImageName) != null) {
        Image maskImage = getImage(maskImageName);
        validateDimensions(inputImage, maskImage);

        String inputImageName = args[0];
        String outputImageName = args[2];
        super.execute(inputImageName, outputImageName);

        Image outputImage = inputImage.applyOperation(
            new MaskOperation(new VisualizeLuma()),
            Arrays.asList(inputImage, maskImage, imageLibrary.getImage(outputImageName))
        );

        addImage(outputImageName, outputImage);
      } else {
        super.execute(args);
      }
    }
  }

  /**
   * Adjusts the brightness of an image selectively using a mask.
   */
  public class BrightenWithMask extends AdjustBrightness {

    /**
     * Constructs a BrightenWithMask operation.
     *
     * @param library the ImageRepo to be used for accessing images
     */
    public BrightenWithMask(ImageRepo library) {
      super(library);
    }

    /**
     * Executes the brighten operation with an optional mask image.
     *
     * @param args the arguments containing brightness factor, input image, mask image, and output image names
     * @throws IllegalArgumentException if dimensions do not match or arguments are invalid
     */
    @Override
    public void execute(String... args) throws IllegalArgumentException {
      int brightnessFactor = Integer.parseInt(args[0]);
      Image inputImage = getImage(args[1]);
      String maskImageName = args[2];

      if (args.length > 3 && imageLibrary.getImage(maskImageName) != null) {
        Image maskImage = getImage(maskImageName);
        validateDimensions(inputImage, maskImage);

        String inputImageName = args[1];
        String outputImageName = args[3];
        super.execute(String.valueOf(brightnessFactor), inputImageName, outputImageName);

        Image outputImage = inputImage.applyOperation(
            new MaskOperation(new VisualizeLuma()),
            Arrays.asList(inputImage, maskImage, imageLibrary.getImage(outputImageName))
        );

        addImage(outputImageName, outputImage);
      } else {
        super.execute(args);
      }
    }
  }

  /**
   * Darkens an image selectively using a mask.
   */
  public class DarkenWithMask extends AdjustBrightness {

    /**
     * Constructs a DarkenWithMask operation.
     *
     * @param library the ImageRepo to be used for accessing images
     */
    public DarkenWithMask(ImageRepo library) {
      super(library);
    }

    /**
     * Executes the darken operation with an optional mask image.
     *
     * @param args the arguments containing brightness factor, input image, mask image, and output image names
     * @throws IllegalArgumentException if dimensions do not match or arguments are invalid
     */
    @Override
    public void execute(String... args) throws IllegalArgumentException {
      args[0] = String.valueOf((-1)*Math.abs(Integer.parseInt(args[0])));

      Image inputImage = getImage(args[1]);
      String maskImageName = args[2];

      if (isMaskImagePresent(args, maskImageName)) {
        applyMaskOperation(args, Integer.parseInt(args[0]), inputImage, maskImageName);
      } else {
        super.execute(args);
      }
    }

    /**
     * Checks if a mask image is provided and present in the repository.
     *
     * @param args           the arguments provided
     * @param maskImageName  the name of the mask image
     * @return true if a mask image is present; false otherwise
     */
    private boolean isMaskImagePresent(String[] args, String maskImageName) {
      return args.length > 3 && imageLibrary.getImage(maskImageName) != null;
    }

    /**
     * Applies the mask operation for darkening the image.
     *
     * @param args              the arguments provided
     * @param brightnessFactor  the brightness adjustment factor
     * @param inputImage        the input image to be darkened
     * @param maskImageName     the name of the mask image
     */
    private void applyMaskOperation(String[] args, int brightnessFactor, Image inputImage, String maskImageName) {
      String inputImageName = args[1];
      String outputImageName = args[3];

      Image maskImage = getImage(maskImageName);
      validateDimensions(inputImage, maskImage);

      super.execute(String.valueOf(brightnessFactor), inputImageName, outputImageName);

      Image outputImage = inputImage.applyOperation(
          new MaskOperation(new VisualizeLuma()),
          Arrays.asList(inputImage, maskImage, imageLibrary.getImage(outputImageName))
      );

      addImage(outputImageName, outputImage);
    }
  }

  /**
   * Validates that the dimensions of the input and mask images are the same.
   *
   * @param inputImage  the input image
   * @param maskImage   the mask image
   * @throws IllegalArgumentException if the dimensions do not match
   */
  private void validateDimensions(Image inputImage, Image maskImage) {
    if (inputImage.getHeight() != maskImage.getHeight() || inputImage.getWidth() != maskImage.getWidth()) {
      throw new IllegalArgumentException("Dimensions should be the same to apply the operation.");
    }
  }
}
