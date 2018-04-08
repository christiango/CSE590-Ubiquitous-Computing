package go.christian.steptracker;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Pair;

import java.util.ArrayList;

public class PacmanDrawable extends Drawable {
  private final Paint _redPaint;
  private final Paint _bluePaint;
  private final Paint _yellowPaint;
  private final Paint _blackPaint;
  private final Paint _pelletPaint;

  private String _pacmanDirection = "R";

  private GridInfo _gridInfo;

  /**
   * A textual representation of the pacman grid. X is the location of pacman - is a horizontal wall
   * | is a vertical wall, 0 is a pellet space represents an empty space
   */
  private ArrayList<String> _grid = new ArrayList<>();

  private void initializeGrid() {
    _grid.add("-----------------------");
    _grid.add("|X0000| |0000000000000|");
    _grid.add("|---|0| |0------------|");
    _grid.add("|   |0| |0000000000000|");
    _grid.add("|---|0| |-----------|0|");
    _grid.add("|00000| |0000000|   |0|");
    _grid.add("|0|-----|0|---|0|   |0|");
    _grid.add("|0|   |000|   |0|   |0|");
    _grid.add("|0|   |0|-|   |0|   |0|");
    _grid.add("|0|   |0|     |0|   |0|");
    _grid.add("|0|   |0|     |0|   |0|");
    _grid.add("|0|---|0|     |0-----0|");
    _grid.add("|0000000|     |0000000|");
    _grid.add("-----------------------");

    _gridInfo = getGridInfo();
  }

  private class GridInfo {
    public int pacmanRow;
    public int pacmanColumn;
    public int pelletCount = 0;
  }

  private GridInfo getGridInfo() {
    GridInfo result = new GridInfo();

    for (int row = 0; row < _grid.size(); row += 1) {
      String line = _grid.get(row);
      for (int column = 0; column < line.length(); column += 1) {
        char character = line.charAt(column);
        if (character == 'X') {
          result.pacmanRow = row;
          result.pacmanColumn = column;
        } else if (character == '0') {
          result.pelletCount += 1;
        }
      }
    }

    return result;
  }

  private char getCharacterAtGridLocation(int row, int column) {
    return _grid.get(row).charAt(column);
  }

  private void setCharacterAtGridLocation(int row, int column, char newValue) {
    char[] chars = _grid.get(row).toCharArray();
    chars[column] = newValue;
    _grid.set(row, new String(chars));
  }

  private void movePacman(int steps) {
    int pacmanRow = _gridInfo.pacmanRow;
    int pacmanColumn = _gridInfo.pacmanColumn;

    for (int i = 1; i <= steps; i += 1) {
      // This algorithm assumes pacman can only have 1 valid move
      setCharacterAtGridLocation(pacmanRow, pacmanColumn, ' ');
      if (getCharacterAtGridLocation(pacmanRow - 1, pacmanColumn) == '0') {
        pacmanRow -= 1;
        _pacmanDirection = "U";
      } else if (getCharacterAtGridLocation(pacmanRow + 1, pacmanColumn) == '0') {
        pacmanRow += 1;
        _pacmanDirection = "D";
      } else if (getCharacterAtGridLocation(pacmanRow, pacmanColumn - 1) == '0') {
        pacmanColumn -= 1;
        _pacmanDirection = "L";
      } else if (getCharacterAtGridLocation(pacmanRow, pacmanColumn + 1) == '0') {
        pacmanColumn += 1;
        _pacmanDirection = "R";
      }

      setCharacterAtGridLocation(pacmanRow, pacmanColumn, 'X');
    }
  }

  public PacmanDrawable() {
    // Set up color and text size
    _redPaint = new Paint();
    _redPaint.setARGB(255, 255, 0, 0);

    _bluePaint = new Paint();
    _bluePaint.setARGB(255, 33, 34, 213);

    _yellowPaint = new Paint();
    _yellowPaint.setARGB(255, 255, 255, 0);

    _blackPaint = new Paint();
    _blackPaint.setARGB(255, 0, 0, 0);

    _pelletPaint = new Paint();
    _pelletPaint.setARGB(255, 220, 166, 142);

    initializeGrid();
  }

  private void drawPacman(Canvas canvas, int xPos, int yPos, int boxSize) {
    canvas.drawRect(xPos, yPos, xPos + boxSize, yPos + boxSize, _blackPaint);
    canvas.drawCircle(xPos + boxSize / 2, yPos + boxSize / 2, boxSize / 2, _yellowPaint);

    Path path = new Path();
    path.moveTo(xPos + boxSize / 2, yPos + boxSize / 2);

    if (_pacmanDirection == "L") {
      path.lineTo(xPos, yPos);
      path.lineTo(xPos, yPos + boxSize);
    } else if (_pacmanDirection == "R") {
      path.lineTo(xPos + boxSize, yPos);
      path.lineTo(xPos + boxSize, yPos + boxSize);
    } else if (_pacmanDirection == "D") {
      path.lineTo(xPos, yPos + boxSize);
      path.lineTo(xPos + boxSize, yPos + boxSize);
    } else if (_pacmanDirection == "U") {
      path.lineTo(xPos, yPos);
      path.lineTo(xPos + boxSize, yPos);
    }

    path.moveTo(xPos + boxSize / 2, yPos + boxSize / 2);
    path.close();

    canvas.drawPath(path, _blackPaint);
  }

  @Override
  public void draw(Canvas canvas) {
    movePacman(10);

    // The size of a
    int boxSize = 40;

    for (int row = 0; row < _grid.size(); row += 1) {
      String line = _grid.get(row);
      for (int column = 0; column < line.length(); column += 1) {
        char character = line.charAt(column);

        int xPos = column * boxSize;
        int yPos = row * boxSize;

        if (character == '-' | character == '|') {
          canvas.drawRect(xPos, yPos, xPos + boxSize, yPos + boxSize, _bluePaint);
        } else if (character == 'X') {
          drawPacman(canvas, xPos, yPos, boxSize);
        } else if (character == '0') {
          canvas.drawRect(xPos, yPos, xPos + boxSize, yPos + boxSize, _blackPaint);
          canvas.drawCircle(
              xPos + boxSize / 2, yPos + boxSize / 2, (float) (boxSize * 0.2), _pelletPaint);
        } else {
          canvas.drawRect(xPos, yPos, xPos + boxSize, yPos + boxSize, _blackPaint);
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
