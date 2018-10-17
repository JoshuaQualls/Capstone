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

    //Set v1 to the special NaN case so we can check this later in computeCalculation()
    private double v1 = Double.NaN;
    private double v2;

    //For easier reading when testing logic
    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char MULTIPLICATION = '*';
    private static final char DIVISION = '/';

    //See what the current computation is
    private char CURRENT;

    //Format display decimal for better interface
    private DecimalFormat d;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Format to only 4 decimal places
        d = new DecimalFormat("#.####");

        //Allow for easier, more direct references to the activity_main
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //Add a "." to the editText view when buttonPeriod is pressed
        binding.buttonPeriod.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + ".");
            }
        });

        //Add a "0" to the editText view when buttonZero is pressed
        binding.buttonZero.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "0");
            }
        });

        //Add a "1" to the editText view when buttonOne is pressed
        binding.buttonOne.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "1");
            }
        });

        //Add a "2" to the editText view when buttonTwo is pressed
        binding.buttonTwo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "2");
            }
        });

        //Add a "3" to the editText view when buttonThree is pressed
        binding.buttonThree.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "3");
            }
        });

        //Add a "4" to the editText view when buttonFour is pressed
        binding.buttonFour.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "4");
            }
        });

        //Add a "5" to the editText view when buttonFive is pressed
        binding.buttonFive.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "5");
            }
        });

        //Add a "6" to the editText view when buttonSix is pressed
        binding.buttonSix.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "6");
            }
        });

        //Add a "7" to the editText view when buttonSeven is pressed
        binding.buttonSeven.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "7");
            }
        });

        //Add an "8" to the editText view when buttonEight is pressed
        binding.buttonEight.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "8");
            }
        });

        //Add a "9" to the editText view when buttonNine is pressed
        binding.buttonNine.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                binding.editText.setText(binding.editText.getText() + "9");
            }
        });

        //For computation buttons, we need to compute any existing calculations first

        //Set CURRENT = ADDITION and move v1 and "+" to the infoTextView from the editText view
        //Remove current text from editText view
        binding.buttonAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                computeCalculation();
                CURRENT = ADDITION;
                binding.infoTextView.setText(d.format(v1) + "+");
                binding.editText.setText(null);
            }
        });

        //Set CURRENT = SUBTRACTION and move v1 and "-" to the infoTextView from the editText view
        //Remove current text from editText view
        binding.buttonSubtract.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                computeCalculation();
                CURRENT = SUBTRACTION;
                binding.infoTextView.setText(d.format(v1) + "-");
                binding.editText.setText(null);
            }
        });

        //Set CURRENT = MULTIPLICATION and move v1 and "*" to the infoTextView from the editText view
        //Remove current text from editText view
        binding.buttonMultiply.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                computeCalculation();
                CURRENT = MULTIPLICATION;
                binding.infoTextView.setText(d.format(v1) + "*");
                binding.editText.setText(null);
            }
        });

        //Set CURRENT = DIVISION and move v1 and "/" to the infoTextView from the editText view
        //Remove current text from editText view
        binding.buttonDivide.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                computeCalculation();
                CURRENT = DIVISION;
                binding.infoTextView.setText(d.format(v1) + "/");
                binding.editText.setText(null);
            }
        });

        //Check if v1 has been instantiated(is not NaN)
        //  - If so: Don't display NaN
        //  - Else: Display the answer to the current computation
        //Change the current to 'O' to avoid continuous computations
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

        //If there is text in editText: remove it
        //Else: change all text views to "" and reset v1 & v2
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

    //If v1 has been instantiated: complete the computation
    //Else: v1 must be equal to what is in the editText view
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
