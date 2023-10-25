package com.example.cells;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
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
    int minesCurrent = 3;
    final int WIDTH = 10;
    final int HEIGHT = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mines = findViewById(R.id.mines);

        mines.setText(""+ minesCurrent +" / "+MINESCONST);

        generate();
        generateMines();

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

        generateCells();

        for(int i=0; i<HEIGHT;i++){
            for(int j=0; j<WIDTH;j++){
                cells[i][j].setText(String.valueOf(1));
                if((i+j)%2 == 0) cells[i][j].setBackgroundColor(ContextCompat.getColor(this, R.color.lightGreen));
                else cells[i][j].setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen));


                cells[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackgroundColor(Color.RED);
                    }
                });
                cells[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        v.setBackgroundColor(Color.BLUE);
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
            }
        }
    }
}