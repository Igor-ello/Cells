package com.example.cells;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView[][] cells;
    TextView mines;

    final int MINESCONST = 15;
    int minesCurrent = MINESCONST;
    final int WIDTH = 10;
    final int HEIGHT = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mines = findViewById(R.id.mines);

        mines.setText(""+ minesCurrent +" / "+MINESCONST);

        generateCells();
        generateMines();
        generate();

    }

    public void generateMines(){
        for(int i=0; i<MINESCONST; i++){
            int positionX = getRandomPosition(HEIGHT);
            int positionY = getRandomPosition(WIDTH);
            cells[positionX][positionY].setText(String.valueOf(-1));
        }
    }

    public int getRandomPosition(int value){
        return new Random().nextInt(value);
    }

    public void generate(){
        GridLayout layout = findViewById(R.id.grid);

        for(int i=0; i<HEIGHT;i++){
            for(int j=0; j<WIDTH;j++){

                if(!cells[i][j].getText().equals("-1"))
                    cells[i][j].setText(String.valueOf(setTextToCell(i, j)));

                cells[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackgroundColor(Color.RED);
                    }
                });
                cells[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setFlagToCell(v);

                        minesCurrent--;
                        mines.setText(String.valueOf(minesCurrent +" / "+MINESCONST));
                        if(minesCurrent==0) {
                            Toast.makeText(getApplicationContext(), "WIN!!!", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                });

                layout.addView(cells[i][j]);
            }
        }
    }

    public int setTextToCell(int i, int j){
        int count = 0;

        int topLock = i - 1, bottomLock = i + 1, rightLock = j + 1, leftLock = j - 1;
        if (j == 0) leftLock = 0;
        else if(j == WIDTH - 1) rightLock = WIDTH - 1;
        if (i == 0) topLock = 0;
        else if (i == HEIGHT - 1) bottomLock = HEIGHT - 1;

        for (int m = topLock; m <= bottomLock; m++) {
            for (int n = leftLock; n <= rightLock; n++) {
                if (cells[m][n].getText().equals("-1")) count++;
            }
        }

        return count;
    }

    public void setFlagToCell(View view){
        Drawable[] layers = new Drawable[2];

        Drawable currentBackground = view.getBackground();
        layers[0] = currentBackground; // Текущий фон
        // Получаем значок (изображение) из ресурсов
        Drawable iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.flag);
        layers[1] = iconDrawable; // Значок
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        // Устанавливаем LayerDrawable как фон элемента View
        view.setBackground(layerDrawable);
    }

    public void setColorToCell(TextView cell, int i, int j){
        if((i+j)%2 == 0) cell.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGreen));
        else cell.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen));
    }

    public void generateCells(){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        GridLayout layout = findViewById(R.id.grid);
        layout.removeAllViews(); // Удаляем все существующие представления в GridLayout (если они есть)
        layout.setColumnCount(WIDTH); // Устанавливаем количество столбцов в GridLayout (WIDTH - константа)

        cells = new TextView[HEIGHT][WIDTH];
        for(int i=0; i<HEIGHT;i++){ // Создаем цикл для создания кнопок (Cells)
            for(int j=0; j<WIDTH;j++){
                // Используем LayoutInflater для надувания макета кнопки из R.layout.cell
                cells[i][j] = (TextView) inflater.inflate(R.layout.cell, layout, false);
                setColorToCell(cells[i][j], i, j);
            }
        }
    }
}