package cz.vaklur.user_permissions

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import cz.vaklur.user_permissions.databinding.ActivityMainBinding
import cz.vaklur.user_permissions.permission.view_model.PermissionViewModel

/**
 * This application was created as part of diploma thesis "Android mobile application security"
 * Created by Jakub Michálek 1.5.2022
 * ID: 186140
 * Brno university of technology
 * 186140@vut.cz
 *
 * @author Jakub Michálek
 */


/**
 * The application main activity based on BaseActivity.
 */
class MainActivity : BaseActivity() {

    // variable that holds information whether the HW back button is allowed to be used
    var allowBackPressed = true

    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionVM: PermissionViewModel

    /**
     * Create main activity and display dialog with warning for user.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        permissionVM = ViewModelProvider(this).get(PermissionViewModel::class.java)
        if (savedInstanceState == null) {
            permissionVM.deleteUserInServer()
            appAlertDialog(binding.root)
        }
    }

    /**
     * Create options menu.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Functions that manage selection of options menu item.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.action_global_settingsFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * A dialog that inform the user that the application may use his sensitive data.
     * <p>
     * If the user:
     * <p>
     * agree: the application starts.
     * <p>
     * disagree: the app is terminated.
     */
    private fun appAlertDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)

        builder.setTitle(R.string.dialog_title)
        builder.setMessage(R.string.dialog_message)

        builder.setPositiveButton(
            R.string.dialog_yes
        ) { _, _ ->
        }

        builder.setNegativeButton(
            R.string.dialog_no
        ) { _, _ ->
            // terminate app
            finish()
        }

        builder.show()
    }


    /**
     * Function that manages actions when user using the HW back button.
     */
    override fun onBackPressed() {
        if (allowBackPressed) {
            val navigationController = Navigation.findNavController(this, R.id.nav_host_fragment)
            when (navigationController.currentDestination?.id) {
                R.id.permissionExampleFragment -> {
                    navigationController.navigate(R.id.permissionTheoryFragment)
                }
                R.id.permissionOfflineExampleFragment -> {
                    navigationController.navigate(R.id.permissionTheoryFragment)
                }
                R.id.permissionTheoryFragment -> {
                    permissionVM.deleteUserTableInServer()
                    navigationController.navigate(R.id.permissionFragment)
                }
                R.id.permissionFragment -> {
                    permissionVM.deleteUserInServer()
                    navigationController.navigate(R.id.mainMenuFragment)
                }
                R.id.settingsFragment -> {
                    if (permissionVM.getDataIsSend()) {
                        permissionVM.saveDataIsSend(false)
                        permissionVM.deleteUserTableInServer()
                        navigationController.navigate(R.id.permissionTheoryFragment)
                    } else {
                        super.onBackPressed()
                    }
                }
                R.id.mainMenuFragment -> {
                    finish()
                }
                else -> {
                    super.onBackPressed()
                }
            }
        }
    }
}