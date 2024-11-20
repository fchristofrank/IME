// TutorialDialog.java
package ime.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TutorialDialog extends JDialog {

  private final String[] tutorialSteps = {
      "<html><div style='font-family: Arial, sans-serif; padding: 10px; background-color: #f0f8ff; border-radius: 10px;'>"
          +
          "<h2 style='color: #4169e1; margin-top: 0;'>Welcome to the Image Editor!</h2>" +
          "<p>This tutorial will guide you through the basic features of the application.</p>" +
          "<p style='font-style: italic;'>Click 'Next' to continue or 'Skip' to exit the tutorial.</p>"
          +
          "</div></html>",

      "<html><div style='font-family: Arial, sans-serif; padding: 10px; background-color: #f0f8ff; border-radius: 10px;'>"
          +
          "<h2 style='color: #4169e1; margin-top: 0;'>Loading and Saving Images</h2>" +
          "<ul style='list-style-type: circle; padding-left: 20px;'>" +
          "<li>Use <b>File → Load</b> to open an image</li>" +
          "<li>Use <b>File → Save</b> to save your edited image</li>" +
          "<li>Supported formats: JPG, PNG, PPM</li>" +
          "</ul>" +
          "</div></html>",

      "<html><div style='font-family: Arial, sans-serif; padding: 10px; background-color: #f0f8ff; border-radius: 10px;'>"
          +
          "<h2 style='color: #4169e1; margin-top: 0;'>Basic Image Operations</h2>" +
          "<p>Use the buttons on the left panel for basic operations:</p>" +
          "<ul style='list-style-type: circle; padding-left: 20px;'>" +
          "<li>Flip images horizontally or vertically</li>" +
          "<li>Apply filters like Blur, Sharpen</li>" +
          "<li>Convert to Sepia or Greyscale</li>" +
          "<li>Extract color components (Red, Green, Blue)</li>" +
          "<li><b>Color Correction:</b> Adjust brightness, contrast, and saturation</li>" +
          "</ul>" +
          "</div></html>",

      "<html><div style='font-family: Arial, sans-serif; padding: 10px; background-color: #f0f8ff; border-radius: 10px;'>"
          +
          "<h2 style='color: #4169e1; margin-top: 0;'>Histogram</h2>" +
          "<ul style='list-style-type: circle; padding-left: 20px;'>" +
          "<li>The histogram is always visible</li>" +
          "<li>It updates dynamically with every operation</li>" +
          "<li>Observe real-time changes in color distribution</li>" +
          "</ul>" +
          "</div></html>",

      "<html><div style='font-family: Arial, sans-serif; padding: 10px; background-color: #f0f8ff; border-radius: 10px;'>"
          +
          "<h2 style='color: #4169e1; margin-top: 0;'>Preview Mode</h2>" +
          "<ul style='list-style-type: circle; padding-left: 20px;'>" +
          "<li>Enable Preview Mode to see changes before applying</li>" +
          "<li style='color: #ff4500;'><b>Important:</b> After entering Preview Mode, you must apply an operation to see the preview</li>"
          +
          "<li>Use the slider to adjust the width of the split view</li>" +
          "<li>Toggle the preview on/off using the enable check box</li>" +
          "<li>Click 'Apply' to make changes permanent</li>" +
          "</ul>" +
          "</div></html>",

      "<html><div style='font-family: Arial, sans-serif; padding: 10px; background-color: #f0f8ff; border-radius: 10px;'>"
          +
          "<h2 style='color: #4169e1; margin-top: 0;'>Advanced Features</h2>" +
          "<ul style='list-style-type: circle; padding-left: 20px;'>" +
          "<li>Compress images by specifying compression percentage</li>" +
          "<li>Adjust levels using Black, Middle, and White points</li>" +
          "<li><b>Downscale:</b> Reduce image size while preserving quality</li>" +
          "</ul>" +
          "</div></html>",

      "<html><div style='font-family: Arial, sans-serif; padding: 10px; background-color: #f0f8ff; border-radius: 10px;'>"
          +
          "<h2 style='color: #4169e1; margin-top: 0;'>Ready to Start!</h2>" +
          "<p>You're now ready to use the Image Editor.</p>" +
          "<p style='font-weight: bold;'>Click 'Finish' to start editing!</p>" +
          "<p style='font-style: italic; color: #228b22;'>Happy Editing!</p>" +
          "</div></html>"
  };
  private int currentStep = 0;

  public TutorialDialog(JFrame parent) {
    super(parent, "Tutorial", true);
    setupDialog();
  }

  private void setupDialog() {
    setSize(400, 300);
    setLocationRelativeTo(getParent());
    setResizable(false);

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Content panel
    JPanel contentPanel = new JPanel(new BorderLayout());
    JLabel contentLabel = new JLabel(tutorialSteps[currentStep]);
    contentPanel.add(contentLabel, BorderLayout.CENTER);
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton skipButton = new JButton("Skip");
    JButton prevButton = new JButton("Previous");
    JButton nextButton = new JButton("Next");
    prevButton.setEnabled(false);

    // Add action listeners
    skipButton.addActionListener(e -> dispose());

    prevButton.addActionListener(e -> {
      currentStep--;
      contentLabel.setText(tutorialSteps[currentStep]);
      updateButtonStates(prevButton, nextButton);
    });

    nextButton.addActionListener(e -> {
      if (currentStep == tutorialSteps.length - 1) {
        dispose(); // Close dialog when "Finish" is clicked
      } else {
        currentStep++;
        contentLabel.setText(tutorialSteps[currentStep]);
        updateButtonStates(prevButton, nextButton);
      }
    });

    buttonPanel.add(skipButton);
    buttonPanel.add(prevButton);
    buttonPanel.add(nextButton);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    add(mainPanel);
  }

  private void updateButtonStates(JButton prevButton, JButton nextButton) {
    prevButton.setEnabled(currentStep > 0);
    nextButton.setText(currentStep == tutorialSteps.length - 1 ? "Finish" : "Next");
  }
}