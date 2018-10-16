package com.example.joshuaqualls.androidproficiency2;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.text.DecimalFormat;

import com.example.joshuaqualls.androidproficiency2.databinding.ActivityMainBinding;





public class MainActivity extends AppCompatActivity{

    //Allows us to directly access the views, as we will only be working with one
    //Removes the needs for constantly referencing id's
    private ActivityMainBinding binding;

    //set v1 to the special NaN case so we can check this later in computeCalculation
    private double v1 = Double.NaN;
    private double v2;

    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char MULTIPLICATION = '*';
    private static final char DIVISION = '/';

    private char CURRENT;

    //Format display decimal for better interface
    private DecimalFormat d;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        d = new DecimalFormat("#.####");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.buttonPeriod.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + ".");
            }
        });

        binding.buttonZero.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "0");
            }
        });

        binding.buttonOne.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "1");
            }
        });

        binding.buttonTwo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "2");
            }
        });

        binding.buttonThree.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "3");
            }
        });

        binding.buttonFour.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "4");
            }
        });

        binding.buttonFive.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "5");
            }
        });

        binding.buttonSix.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "6");
            }
        });

        binding.buttonSeven.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "7");
            }
        });

        binding.buttonEight.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "8");
            }
        });

        binding.buttonNine.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "9");
            }
        });

        //For computation buttons, we need to compute any existing calculations first

        binding.buttonAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                computeCalculation();
                CURRENT = ADDITION;
                binding.infoTextView.setText(d.format(v1) + "+");
                binding.editText.setText(null);
            }
        });

        binding.buttonSubtract.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                computeCalculation();
                CURRENT = SUBTRACTION;
                binding.infoTextView.setText(d.format(v1) + "-");
                binding.editText.setText(null);
            }
        });

        binding.buttonMultiply.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                computeCalculation();
                CURRENT = MULTIPLICATION;
                binding.infoTextView.setText(d.format(v1) + "*");
                binding.editText.setText(null);
            }
        });

        binding.buttonDivide.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                computeCalculation();
                CURRENT = DIVISION;
                binding.infoTextView.setText(d.format(v1) + "/");
                binding.editText.setText(null);
            }
        });

        binding.buttonEqual.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                computeCalculation();
                if(Double.isNaN(v1)) {
                    //Don't display NaN
                    binding.infoTextView.setText("");
                }
                else {
                    //Display the answer
                    binding.infoTextView.setText(d.format(v1));
                }
                v1 = Double.NaN;
                CURRENT = 'O';
            }
        });

        binding.buttonClear.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(binding.editText.getText().length() > 0){
                    String currentText = binding.editText.getText().toString();
                    binding.editText.setText(currentText.substring(0, currentText.length()-1));
                }
                else{
                    v1 = Double.NaN;
                    v2 = Double.NaN;
                    binding.editText.setText("");
                    binding.infoTextView.setText("");
                }
            }
        });
    }
    private void computeCalculation() {
        if (! Double.isNaN(v1)) {
            //grab the string in the editText view and use that as your v2
            //Because of how we define v1 as NaN, we know that v2 will always be the editText text
            v2 = Double.parseDouble(binding.editText.getText().toString());
            binding.editText.setText(null);

            if (CURRENT == ADDITION) {
                v1 = this.v1 + v2;
            } else if (CURRENT == SUBTRACTION) {
                v1 = this.v1 - v2;
            } else if (CURRENT == MULTIPLICATION) {
                v1 = this.v1 * v2;
            } else if (CURRENT == DIVISION) {
                v1 = this.v1 / v2;
            }
        }
        else {
            v1 = Double.parseDouble(binding.editText.getText().toString());
        }
    }
}
