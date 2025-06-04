package it.unisa.diem.sad.geoshapes.model.shapes;

import it.unisa.diem.sad.geoshapes.model.MyColor;
import it.unisa.diem.sad.geoshapes.model.shapes.MyEllipse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test suite for the {@link MyEllipse} class.
 * This suite verifies the core business logic of the ellipse shape, including
 * its construction, shape type identification, and geometric transformations
 * like horizontal and vertical flipping.
 */
public class MyEllipseTest {

    private static final double DELTA = 1e-9; // Tolerance for double comparisons

    private MyColor redColor;
    private MyColor blueColor;

    @BeforeEach
    void setUp() {
        redColor = new MyColor(1.0, 0.0, 0.0, 1.0);
        blueColor = new MyColor(0.0, 0.0, 1.0, 0.5);
    }

    /**
     * Tests the constructor and basic getter methods for correct initialization.
     * Verifies that coordinates, rotation, and colors are set as expected.
     */
    @Test
    void constructorAndGettersWorkCorrectly() {
        double startX = 0.1;
        double startY = 0.2;
        double endX = 0.5;
        double endY = 0.7;
        double rotation = 45.0;

        MyEllipse ellipse = new MyEllipse(startX, startY, endX, endY, rotation, redColor, blueColor);

        assertNotNull(ellipse);
        assertEquals(startX, ellipse.getStartX(), DELTA);
        assertEquals(startY, ellipse.getStartY(), DELTA);
        assertEquals(endX, ellipse.getEndX(), DELTA);
        assertEquals(endY, ellipse.getEndY(), DELTA);
        assertEquals(rotation, ellipse.getRotation(), DELTA);
        assertEquals(redColor, ellipse.getBorderColor());
        assertEquals(blueColor, ellipse.getFillColor());
    }

    /**
     * Tests that the `getShapeType` method returns the correct string "Ellipse".
     */
    @Test
    void getShapeTypeReturnsEllipse() {
        MyEllipse ellipse = new MyEllipse(0, 0, 1, 1, 0, redColor, blueColor);
        assertEquals("Ellipse", ellipse.getShapeType());
    }

    /**
     * Tests the `flipHorizontal` method for a typical ellipse with positive coordinates and no rotation.
     * Verifies that the X-coordinates are correctly reflected across the center.
     */
    @Test
    void flipHorizontalReflectsXCoordinatesForTypicalCase() {
        MyEllipse ellipse = new MyEllipse(0.1, 0.2, 0.5, 0.7, 0, redColor, blueColor); // Center X = 0.3
        ellipse.flipHorizontal();

        assertEquals(0.5, ellipse.getStartX(), DELTA); // New startX should be old endX
        assertEquals(0.1, ellipse.getEndX(), DELTA);   // New endX should be old startX
        assertEquals(0.2, ellipse.getStartY(), DELTA); // Y-coordinates unchanged
        assertEquals(0.7, ellipse.getEndY(), DELTA);   // Y-coordinates unchanged
        assertEquals(0, ellipse.getRotation(), DELTA); // Rotation remains 0 for 0 initial rotation
    }

    /**
     * Tests `flipHorizontal` for an ellipse with negative X-coordinates.
     * Ensures reflection works correctly across the center, even with negative values.
     */
    @Test
    void flipHorizontalReflectsXCoordinatesForNegativeCoordinates() {
        MyEllipse ellipse = new MyEllipse(-0.5, 0.2, -0.1, 0.7, 0, redColor, blueColor); // Center X = -0.3
        ellipse.flipHorizontal();

        assertEquals(-0.1, ellipse.getStartX(), DELTA);
        assertEquals(-0.5, ellipse.getEndX(), DELTA);
        assertEquals(0.2, ellipse.getStartY(), DELTA);
        assertEquals(0.7, ellipse.getEndY(), DELTA);
        assertEquals(0, ellipse.getRotation(), DELTA);
    }

    /**
     * Tests `flipHorizontal` for a degenerate ellipse (zero width).
     * Ensures coordinates are handled without issues.
     */
    @Test
    void flipHorizontalHandlesZeroWidthEllipse() {
        MyEllipse ellipse = new MyEllipse(0.5, 0.2, 0.5, 0.7, 0, redColor, blueColor); // Zero width
        ellipse.flipHorizontal();

        assertEquals(0.5, ellipse.getStartX(), DELTA);
        assertEquals(0.5, ellipse.getEndX(), DELTA);
        assertEquals(0.2, ellipse.getStartY(), DELTA);
        assertEquals(0.7, ellipse.getEndY(), DELTA);
        assertEquals(0, ellipse.getRotation(), DELTA);
    }

    /**
     * Tests `flipHorizontal` with a non-zero initial rotation.
     * Verifies that the rotation is correctly negated for a horizontal flip.
     */
    @Test
    void flipHorizontalNegatesRotationForNonZeroRotation() {
        MyEllipse ellipse = new MyEllipse(0.1, 0.2, 0.5, 0.7, 30.0, redColor, blueColor);
        ellipse.flipHorizontal();
        assertEquals(-30.0, ellipse.getRotation(), DELTA);

        ellipse.setRotation(-60.0);
        ellipse.flipHorizontal();
        assertEquals(60.0, ellipse.getRotation(), DELTA);
    }


    /**
     * Tests the `flipVertical` method for a typical ellipse with positive coordinates and no rotation.
     * Verifies that the Y-coordinates are correctly reflected across the center.
     */
    @Test
    void flipVerticalReflectsYCoordinatesForTypicalCase() {
        MyEllipse ellipse = new MyEllipse(0.1, 0.2, 0.5, 0.7, 0, redColor, blueColor); // Center Y = 0.45
        ellipse.flipVertical();

        assertEquals(0.1, ellipse.getStartX(), DELTA); // X-coordinates unchanged
        assertEquals(0.5, ellipse.getEndX(), DELTA);   // X-coordinates unchanged
        assertEquals(0.7, ellipse.getStartY(), DELTA); // New startY should be old endY
        assertEquals(0.2, ellipse.getEndY(), DELTA);   // New endY should be old startY
        assertEquals(0, ellipse.getRotation(), DELTA); // Rotation remains 0 for 0 initial rotation
    }

    /**
     * Tests `flipVertical` for an ellipse with negative Y-coordinates.
     * Ensures reflection works correctly across the center, even with negative values.
     */
    @Test
    void flipVerticalReflectsYCoordinatesForNegativeCoordinates() {
        MyEllipse ellipse = new MyEllipse(0.1, -0.5, 0.5, -0.1, 0, redColor, blueColor); // Center Y = -0.3
        ellipse.flipVertical();

        assertEquals(0.1, ellipse.getStartX(), DELTA);
        assertEquals(0.5, ellipse.getEndX(), DELTA);
        assertEquals(-0.1, ellipse.getStartY(), DELTA);
        assertEquals(-0.5, ellipse.getEndY(), DELTA);
        assertEquals(0, ellipse.getRotation(), DELTA);
    }



    /**
     * Tests `flipVertical` for a degenerate ellipse (zero height).
     * Ensures coordinates are handled without issues.
     */
    @Test
    void flipVerticalHandlesZeroHeightEllipse() {
        MyEllipse ellipse = new MyEllipse(0.1, 0.5, 0.5, 0.5, 0, redColor, blueColor); // Zero height
        ellipse.flipVertical();

        assertEquals(0.1, ellipse.getStartX(), DELTA);
        assertEquals(0.5, ellipse.getEndX(), DELTA);
        assertEquals(0.5, ellipse.getStartY(), DELTA);
        assertEquals(0.5, ellipse.getEndY(), DELTA);
        assertEquals(0, ellipse.getRotation(), DELTA);
    }

    /**
     * Tests `flipVertical` with a non-zero initial rotation.
     * Verifies that the rotation is correctly adjusted by 180 degrees for a vertical flip.
     */
    @Test
    void flipVerticalAdjustsRotationForNonZeroRotation() {
        MyEllipse ellipse = new MyEllipse(0.1, 0.2, 0.5, 0.7, 30.0, redColor, blueColor);
        ellipse.flipVertical();
        assertEquals(150.0, ellipse.getRotation(), DELTA); // 180 - 30 = 150

        ellipse.setRotation(-60.0);
        ellipse.flipVertical();
        assertEquals(240.0 - 360.0, ellipse.getRotation(), DELTA); // 180 - (-60) = 240, normalizes to -120
    }





}