/*
 * Copyright 2021 martin.vagner.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.stv.neurondemofx;


import java.io.Serializable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
/**
 *
 * @author martin.vagner
 */
public class Image implements Serializable
{
  
  public int width, height;
  
  public byte[][] matrix;
  
  public Image(int width , int height)
  {
    this.width = width;
    this.height = height;
    matrix = new byte[width][height];    
    
  }
  
  public Image ( BufferedImage img )
  {
    
  }
  
  public int getWidth()
  {
    return matrix.length;
  }
  
  public int getHeight()
  {
    return matrix[0].length;
  }
  
  
  public static Image load(Reader reader)
  {
    return new Image(100 , 100);
  }
  
  public void erase ()
  {
    for ( int x = 0 ; x < getWidth() ; x ++)
      for ( int y = 0 ; y < getHeight() ; y ++)
        matrix[x][y] = 0;
  }
  
  public void setPixel ( int x , int y , byte value)
  {
    matrix[x][y] = value;
  }
  
  public void save (Writer writer) throws IOException
  {
    
  }
  
  public void draw(ImageDrawable imageDrawable)
  {
    imageDrawable.draw(matrix);
    
  }
}
