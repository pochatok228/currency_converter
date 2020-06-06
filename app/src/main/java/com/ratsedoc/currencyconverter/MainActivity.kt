package com.ratsedoc.currencyconverter

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.ResponseCache
import java.net.URL
import kotlin.concurrent.thread



class MainActivity : AppCompatActivity() {

    private var currency_string_1 : String = "";
    private var currency_string_2 : String = "";
    private var course_value : Double = 0.00;

    private var button1Ids : ArrayList<Int> = arrayListOf(R.id.buttonRus1, R.id.buttonUsa1, R.id.buttonEu1, R.id.buttonUkraine1, R.id.buttonUk1)
    private var button2Ids : ArrayList<Int> = arrayListOf(R.id.buttonRus2, R.id.buttonUsa2, R.id.buttonEu2, R.id.buttonUkraine2, R.id.buttonUk2)


    @SuppressLint("ResourceAsColor")
    private fun button1Pressed(button : View)
    {
        for (element_id in button1Ids)
        {
            findViewById<ImageButton>(element_id).setBackgroundResource(R.color.colorPrimaryDark)
        }
        button.setBackgroundResource(R.color.colorPrimary);

        var new_currency_name : String = when (button.id)
        {
            buttonRus1.id -> "RUB";
            buttonUsa1.id -> "USD";
            buttonEu1.id -> "EUR";
            buttonUkraine1.id ->"ILS";
            buttonUk1.id -> "GBP";
            else -> "";
        }

        currency1.setText(new_currency_name);
        currency_string_1 = new_currency_name;

        changeCourse();

    }

    private fun button2Pressed(button : View)
    {
        for (element_id in button2Ids)
        {
            findViewById<ImageButton>(element_id).setBackgroundResource(R.color.colorPrimaryDark)
        }
        button.setBackgroundResource(R.color.colorPrimary);

        var new_currency_name : String = when (button.id)
        {
            buttonRus2.id -> "RUB";
            buttonUsa2.id -> "USD";
            buttonEu2.id -> "EUR";
            buttonUkraine2.id ->"ILS";
            buttonUk2.id -> "GBP";
            else -> "";
        }

        currency2.setText(new_currency_name);
        currency_string_2 = new_currency_name;

        changeCourse();
    }

    private fun changeCourse()
    {
        course_value = getCourseValue(currency_string_1, currency_string_2) ?: 1.0;
        var remember : String = editText1.text.toString();
    }

    fun sendGet(string1 : String, string2 : String) : String? {
        if (string1 == string2) return "{\"rates\":{\"$string1\":1.0},\"base\":\"$string2\",\"date\":\"2020-06-05\"}";
        val url = URL("https://api.exchangeratesapi.io/latest?symbols=$string1&base=$string2")
        Log.d("Course", "https://api.exchangeratesapi.io/latest?symbols=$string1&base=$string2")

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET

            // Log.d("Course", "\nSent 'GET' request to URL : $url; Response Code : $responseCode")

            var return_string : String = ""
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    return_string += line.toString(); Log.d("Course", line.toString())
                }
                return return_string;
            }
        }
        return null;
    }

    private fun getCourseValue(cur1 : String, cur2 : String) : Double?
    {
        thread {
            try {
                var response : String = sendGet(currency_string_1, currency_string_2)  ?: "";
                response = response.split("{")[2].split("}")[0].split(':')[1]
                Log.d("Course",response)
                course_value = response.toDouble();
                course.setText(course_value.toString());
                var num_to_set : Double = (try{editText1.text.toString().toDouble()} catch (e : Exception) {0.00;} / course_value)


                editText2.setText(String.format("%.2f", num_to_set));
            }

            catch (e : Exception) {Log.d("Course", e.toString())}
        }


        return null;
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide();

        for (button_id in button1Ids) findViewById<ImageButton>(button_id).setOnClickListener(this :: button1Pressed);
        for (button_id in button2Ids) findViewById<ImageButton>(button_id).setOnClickListener(this :: button2Pressed);

        button1Pressed(buttonRus1);
        button2Pressed(buttonUsa2);

        editText1.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                var field_value : Double = try{s.toString().toDouble();} catch (e : Exception) {0.0};
                var another_field_value = field_value / course_value;
                if (another_field_value == try{editText2.text.toString().toDouble()} catch (e : Exception) {0.0;}) {}
                else {
                    editText2.setText(String.format("%.2f", another_field_value));};
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


    }
}