package ime.model.pixel;

/**
 * This abstract class represents a pixel, holding the values for the red, green, and blue
 * components of a pixel. It serves as a base class for specific pixel implementations
 * (such as RGBPixel and RGBAPixel) that define how color pixels are represented and accessed.
 */
public abstract class AbstractPixel implements Pixel {

  /**
   * This method normalizes pixel channel values to ensure they remain within the valid range.
   *
   * @param value the value of the channel which has to be clamped.
   * @return the clamped value of the channel.
   */
  public abstract int clamp(int value);

}
