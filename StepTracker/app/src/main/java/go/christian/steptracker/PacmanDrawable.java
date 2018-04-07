package go.christian.steptracker;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class PacmanDrawable extends Drawable {
  private final Paint _redPaint;
  private final Paint _bluePaint;
  private final Paint _yellowPaint;

  /**
   * A textual representation of the pacman grid. X is the location of pacman - is a horizontal wall
   * | is a vertical wall, 0 is a pellet space represents an empty space
   */
  private ArrayList<String> _grid = new ArrayList<>();

  private void initializeGrid() {
    _grid.add("--------------------");
    _grid.add("|X0000| |0000000000|");
    _grid.add("|---|0| |0---------|");
    _grid.add("|   |0| |0000000000|");
    _grid.add("|   |0| |--------|0|");
    _grid.add("|00000| |0000|   |0|");
    _grid.add("|0|---- |0--0|   |0|");
    _grid.add("|000000000--0000000|");
    _grid.add("--------------------");
  }

  public PacmanDrawable() {
    // Set up color and text size
    _redPaint = new Paint();
    _redPaint.setARGB(255, 255, 0, 0);

    _bluePaint = new Paint();
    _bluePaint.setARGB(255, 0, 0, 255);

    _yellowPaint = new Paint();
    _yellowPaint.setARGB(255, 0, 255, 255);

    initializeGrid();
  }

  @Override
  public void draw(Canvas canvas) {
    // The size of a
    int boxSize = 30;

    for (int row = 0; row < _grid.size(); row += 1) {
      String line = _grid.get(row);
      for (int column = 0; column < line.length(); column += 1) {
        char character = line.charAt(column);

        int xPos = column * boxSize;
        int yPos = row * boxSize;

        if (character == '-' | character == '|') {
          canvas.drawRect(xPos, yPos, xPos + boxSize, yPos + boxSize, _bluePaint);
        } else if (character == 'X') {
          canvas.drawRect(xPos, yPos, xPos + boxSize, yPos + boxSize, _yellowPaint);
        } else if (character == '0') {
          canvas.drawRect(xPos, yPos, xPos + boxSize, yPos + boxSize, _redPaint);
        }
      }
    }
  }

  @Override
  public void setAlpha(int alpha) {
    // This method is required
  }

  @Override
  public void setColorFilter(ColorFilter colorFilter) {
    // This method is required
  }

  @Override
  public int getOpacity() {
    // Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE
    return PixelFormat.OPAQUE;
  }
}
