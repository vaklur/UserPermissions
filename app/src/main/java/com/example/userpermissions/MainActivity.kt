package com.example.userpermissions

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.ConfigurationCompat
import androidx.navigation.Navigation
import com.example.userpermissions.databinding.ActivityMainBinding
import com.example.userpermissions.settings.LocaleUtil
import com.example.userpermissions.settings.SettingsSharPref
import com.example.userpermissions.volley_communication.CommunicationFunction

class MainActivity : AppCompatActivity() {

    private val comFun = CommunicationFunction()
    private lateinit var binding: ActivityMainBinding




    override fun onCreate(savedInstanceState: Bundle?) {

        val actualLocaleCode = SettingsSharPref(this).getLanguageSettings()
        LocaleUtil.applyLocalizedContext(this, actualLocaleCode)
        Log.d("test", actualLocaleCode)
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        resetTitle()

        appAlertDialog(binding.root)

    }

    private fun resetTitle() {
        try {
            val label = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA).labelRes
            if (label != 0) {
                setTitle(label)
            }
        } catch (e: PackageManager.NameNotFoundException) {}
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * A dialog that inform the user that the application may use his sensitive data.
     * <p>
     * If the user:
     * <p>
     * agree: the application starts and the connection to the server is verified.
     * <p>
     * disagree: the app is terminated.
     *
     * @param view Need a View class for show the dialog to user in activity.
     */
    private fun appAlertDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)

        builder.setTitle(R.string.dialog_title)
        builder.setMessage(R.string.dialog_message)

        builder.setPositiveButton(
            R.string.dialog_yes) { _, _ ->
            // Test connection to server
            CommunicationFunction().connectionToServer(this,binding.root)
        }

        builder.setNegativeButton(
            R.string.dialog_no) { _, _ ->
            // terminate app
            finish()
        }

        builder.show()
    }

    override fun onBackPressed() {

        val navigationController = Navigation.findNavController(this, R.id.nav_host_fragment)
        when (navigationController.currentDestination?.id) {
            R.id.PermissionExampleFragment -> {
                /*val bundle = Bundle()
                bundle.putBoolean("dataIsSend",true)
                navigationController.navigate(R.id.PermissionTheoryFragment,bundle)*/
            }
            R.id.PermissionTheoryFragment -> {
                navigationController.navigate(R.id.permissionFragment)
            }
            R.id.permissionFragment -> {
                navigationController.navigate(R.id.mainMenuFragment)
            }
            R.id.mainMenuFragment -> {
                finish()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("test","On Destroy Main Activity")
        comFun.deleteUserInServer(this)

    }
}