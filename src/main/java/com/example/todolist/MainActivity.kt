package com.example.todolist

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.databinding.TaskAdderBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var arrayList:ArrayList<ModelTask>

    private lateinit var firebaseAuth:FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.addTask.setOnClickListener {
            addTaskDialog()
        }
        loadData()
        binding.logout.setOnClickListener {

        }
    }

    private var hour = 0
    private var minute = 0

    private fun addTaskDialog() {
        val taskAddBinding = TaskAdderBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(taskAddBinding.root)

        val alertDialog = builder.create()
        alertDialog.show()

        taskAddBinding.backBlack.setOnClickListener {
            alertDialog.dismiss()
        }

        taskAddBinding.dateEt.setOnClickListener{
            showDatePicker(taskAddBinding.dateEt)
        }

        taskAddBinding.timeEt.setOnClickListener{
            showTimePicker(taskAddBinding.timeEt)
        }

        taskAddBinding.submitBtn.setOnClickListener {
            val title=taskAddBinding.titleEt.text
            val date=taskAddBinding.dateEt.text
            val time=taskAddBinding.timeEt.text
            if (TextUtils.isEmpty(title)){
                Toast.makeText(this,"Enter a title", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(date)){
                Toast.makeText(this,"Enter an date", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(time)){
                Toast.makeText(this,"Enter a time", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog.setMessage("Saving Task")
                saveData(alertDialog,title.toString(),date.toString(),time.toString())

            }
            }
        }

    private fun saveData(alertDialog: AlertDialog?, title: String, date: String, time: String) {
        val user=firebaseAuth.currentUser!!
        val timestamp="${System.currentTimeMillis()}"
        var day=""
        if (date.length==9){
            day=date.substring(0,5)+"0"+date.substring(5)
        }
        val result=day+" "+time+":00"
        val value= convertDateTimeToTimestamp(result, "yyyy-MM-dd HH:mm:ss")

        val hashMap=HashMap<String,Any>()
        hashMap["title"]=title
        hashMap["date"]=date
        hashMap["time"]=time
        hashMap["timestamp"]= value

        val ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.child(user.uid).child("Tasks").child(timestamp).setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                alertDialog!!.dismiss()
                Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show()
                loadData()
            }
            .addOnFailureListener { e->
                Toast.makeText(this, "Task not saved due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    fun convertDateTimeToTimestamp(dateTimeString: String, format: String): Long {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val date = sdf.parse(dateTimeString)
        return date?.time ?: 0L
    }

    private fun loadData() {
        arrayList=ArrayList()
        val user=firebaseAuth.currentUser
        val ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.child(user!!.uid).child("Tasks")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrayList.clear()
                    for(ds in snapshot.children){
                        val model=ds.getValue(ModelTask::class.java)
                        arrayList.add(model!!)
                    }
                    val result=arrayList.sortedByDescending { it.timestamp }
                    val get=ArrayList(result)


                    taskAdapter=TaskAdapter(this@MainActivity,get)
                    binding.taskList.adapter=taskAdapter

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }


    private fun showTimePicker(timeEt: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Create a TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                val timeString = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                timeEt.setText(timeString)
            },
            currentHour,
            currentMinute,
            false
        )

        // Show the TimePickerDialog
        timePickerDialog.show()
    }

    private fun showDatePicker(dateEt: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // Set the selected date on the EditText
                dateEt.setText("$year-${monthOfYear + 1}-$dayOfMonth")
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}