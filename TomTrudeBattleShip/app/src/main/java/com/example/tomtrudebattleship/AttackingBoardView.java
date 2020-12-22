package com.example.tomtrudebattleship;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class AttackingBoardView extends AppCompatImageView {

  public static int cellWidth;
  Paint paint;

  public AttackingBoardView( Context context, AttributeSet attrs ){
    super( context, attrs);
    paint = new Paint();
    paint.setColor( Color.RED );
    paint.setStrokeWidth( 5 );
    paint.setStyle( (Paint.Style.FILL_AND_STROKE) );

    Typeface typeface = Typeface.create( Typeface.SANS_SERIF, Typeface.BOLD );
    paint.setTypeface( typeface );
    paint.setTextAlign( Paint.Align.CENTER );

  }

  @Override
  protected  void onDraw( Canvas canvas) {
    int height = canvas.getHeight() - 1;
    int width = canvas.getWidth() - 1;
    cellWidth = width / 11;
    int cellHeight = height / 11;

    paint.setTextSize( cellWidth );

    //Draw a box around the entire image element with lines
    //    canvas.drawLine( 0, 0, width, 0, paint );
    //    canvas.drawLine( 0, 0, 0, height, paint );
    //    canvas.drawLine( 0, height, width, height, paint );
    //    canvas.drawLine( width, 0, width, height, paint );

    // Draw the grid

    for( int i = 0; i < 12; i++ ) {
      canvas.drawLine( 0, ( cellWidth * i ), width, ( cellWidth * i ), paint );
      canvas.drawLine( ( cellWidth * i ), 0, ( cellWidth * i ), width, paint );
    }

    //Assign points to the double array of gamecell objects
    for( int y = 0; y < 11; y++ ) {
      for( int x = 0; x < 11; x++ ) {
        BoardSetup.attackingGrid[x][y].setTopLeft( new Point( x * cellWidth, y * cellWidth ) );
        BoardSetup.attackingGrid[x][y].setBottomRight( new Point( ( x + 1 ) * cellWidth, ( y + 1 ) * cellWidth ) );
      }
    }

    Rect textBounds = new Rect();
    paint.getTextBounds( "A", 0, 1, textBounds );

    int textHeight = textBounds.height();
    int textWidth = textBounds.width();

    int textX = cellWidth / 2;
    int textY = cellWidth + ( ( cellWidth / 2 ) - ( textHeight - 6 ) );

    //Labels Down

    textY += cellWidth;
    canvas.drawText( "A", textX, textY, paint );
    textY += cellWidth;
    canvas.drawText( "B", textX, textY, paint );
    textY += cellWidth;
    canvas.drawText( "C", textX, textY, paint );
    textY += cellWidth;
    canvas.drawText( "D", textX, textY, paint );
    textY += cellWidth;
    canvas.drawText( "E", textX, textY, paint );
    textY += cellWidth;
    canvas.drawText( "F", textX, textY, paint );
    textY += cellWidth;
    canvas.drawText( "G", textX, textY, paint );
    textY += cellWidth;
    canvas.drawText( "H", textX, textY, paint );
    textY += cellWidth;
    canvas.drawText( "I", textX, textY, paint );
    textY += cellWidth;
    canvas.drawText( "J", textX, textY, paint );

    textX = cellWidth / 2;
    textY = cellWidth + ( ( cellWidth / 2 ) - ( textHeight - 6 ) );

    //Labels Across

    textX += cellWidth;
    canvas.drawText( "1", textX, textY, paint );
    textX += cellWidth;
    canvas.drawText( "2", textX, textY, paint );
    textX += cellWidth;
    canvas.drawText( "3", textX, textY, paint );
    textX += cellWidth;
    canvas.drawText( "4", textX, textY, paint );
    textX += cellWidth;
    canvas.drawText( "5", textX, textY, paint );
    textX += cellWidth;
    canvas.drawText( "6", textX, textY, paint );
    textX += cellWidth;
    canvas.drawText( "7", textX, textY, paint );
    textX += cellWidth;
    canvas.drawText( "8", textX, textY, paint );
    textX += cellWidth;
    canvas.drawText( "9", textX, textY, paint );
    textX += cellWidth;
    canvas.drawText( "10", textX, textY, paint );

    //Walk through the double array of gamecells and draw whatever is needed in each cell

    float w = paint.measureText( "W", 0, 0 );
    float center = (cellWidth / 2) - (w / 2);

    for( int y = 0; y < 11; y++ ) {
      for( int x = 0; x < 11; x++ ) {
        if( BoardSetup.attackingGrid[x][y].getHasShip() ) {
          //Draw S in the Cell
          drawcell( "S", x, y, center, canvas );
        } else if( BoardSetup.attackingGrid[x][y].getHit() ) {
          // Draw an * in the cell
          drawcell( "*", x, y, center, canvas );
        } else if( BoardSetup.attackingGrid[x][y].getMiss() ) {
          // Draw an - in the cell
          drawcell( "-", x, y, center, canvas );
        } else if( BoardSetup.attackingGrid[x][y].getWaiting() ) {
          // Draw an W in the cell
          drawcell( "W", x, y, center, canvas );
        }
      }
    }
  }

  void drawcell( String contents, int x, int y, float center, Canvas canvas){
    canvas.drawText( contents, BoardSetup.attackingGrid[x][y].getTopLeft().x + center, BoardSetup.attackingGrid[x][y].getBottomRight().y - 12, paint );
  }
}
