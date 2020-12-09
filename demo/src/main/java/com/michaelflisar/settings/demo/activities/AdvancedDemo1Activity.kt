package com.michaelflisar.settings.demo.activities

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michaelflisar.settings.core.Settings
import com.michaelflisar.settings.core.SettingsManager
import com.michaelflisar.settings.core.SettingsUtils
import com.michaelflisar.settings.core.classes.*
import com.michaelflisar.settings.core.enums.ChangeType
import com.michaelflisar.settings.core.enums.CustomLayoutStyle
import com.michaelflisar.settings.core.interfaces.ISetting
import com.michaelflisar.settings.core.interfaces.ISettingsChangedCallback
import com.michaelflisar.settings.core.interfaces.ISettingsData
import com.michaelflisar.settings.core.decorator.Style
import com.michaelflisar.settings.demo.R
import com.michaelflisar.settings.demo.advanced.DBManager
import com.michaelflisar.settings.demo.advanced.DBSettingsData
import com.michaelflisar.settings.demo.advanced.SettingsDefinitions
import com.michaelflisar.settings.demo.advanced.data.db.DesktopWithFolders
import com.michaelflisar.settings.demo.advanced.data.db.FolderWithFolders
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBDesktopItem
import com.michaelflisar.settings.demo.advanced.data.db.entities.DBFolderItem
import com.michaelflisar.settings.demo.advanced.settings.SettDesktopColor
import com.michaelflisar.settings.demo.advanced.settings.SettDesktopLabel
import com.michaelflisar.settings.demo.advanced.ui.DesktopItem
import com.michaelflisar.settings.demo.advanced.ui.FolderItem
import com.michaelflisar.settings.demo.databinding.ActivityAdvancedDemoBinding
import com.michaelflisar.settings.demo.databinding.DialogSettingsBinding
import com.michaelflisar.settings.utils.SettingsData
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdvancedDemo1Activity : AppCompatActivity() {

    lateinit var binding: ActivityAdvancedDemoBinding

    val SETUP by lazy {
        SettingsDisplaySetup(
                customLayout = CustomLayoutStyle.Compact,
                style = SettingsStyle(
                        Style.Flat,
                        Style.Flat,
                        SettingsStyle.GroupStyle(
                                subGroupStyle = SettingsStyle.SubGroupColorMode.Full(0.0f),
                                color = SettingsColor.Color(SettingsUtils.attrColor(this, android.R.attr.colorBackground)),
                                textColor = SettingsColor.Color(SettingsUtils.attrColor(this, android.R.attr.textColorSecondary))
                        ),
                        SettingsStyle.ItemStyle()
                )
        )
    }

    // state
    private var selectedDesktop: DesktopWithFolders? = null
    private var selectedFolders: MutableList<FolderWithFolders> = ArrayList()

    // adapters
    private val desktopItemAdapter = ItemAdapter<DesktopItem>()
    private val desktopFastAdapter = FastAdapter.with(desktopItemAdapter)
    private val folderItemAdapter = ItemAdapter<FolderItem>()
    private val folderFastAdapter = FastAdapter.with(folderItemAdapter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvancedDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        initUI()

        // ---------------
        // Info:
        // this demo does not restore selected desktop / folder state to keep this example easy
        // ---------------

        SettingsManager.registerCallback(object : ISettingsChangedCallback {
            override fun onSettingChanged(changeType: ChangeType, setting: ISetting<*>, settingsData: ISettingsData, oldValue: Any?, newValue: Any?) {
                if (setting == SettDesktopLabel || setting == SettDesktopColor)
                    onDesktopSettingsChanged()
                else
                    onFolderSettingsChanged()
            }
        })

        loadDesktops()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.advanced_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_global_desktop_settings -> {
                showSettingsDialog(SettingsData.Global, true, "GLOBAL desktop settings")
                true
            }
            R.id.menu_global_folder_settings -> {
                showSettingsDialog(SettingsData.Global, false, "GLOBAL folder settings")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        if (selectedFolders.size > 0) {
            selectedFolders.removeLast()
            updateUI()
            return
        }
        if (selectedDesktop != null) {
            selectedDesktop = null
            updateUI()
            return
        }
        super.onBackPressed()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        // we have a custom setting that works with system intents instead of a dialog
//        // => SettingsManager is prepared for this but you need to forward this result to it manually like following:
//        SettingsManager.pushDialogEvent(SettingsActivityResultEvent(requestCode, resultCode, data))
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    // ------------
    // data loading
    // ------------

    private fun loadDesktops() {
        lifecycleScope.launch {
            // 1) load data on IO thread
            withContext(Dispatchers.IO) {
                val desktops = DBManager.get().desktopDao().getAll()
                val desktopItems = desktops.map { DesktopItem(it) }
                // 2) update UI on main thread
                withContext(Dispatchers.Main) {
                    desktopItemAdapter.setNewList(desktopItems)
                    updateUI()
                }
            }
        }
    }

    private fun loadDesktop(id: Long) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                selectedDesktop = DBManager.get().desktopDao().getWithData(id)
                selectedFolders.clear()
                withContext(Dispatchers.Main) {
                    updateUI()
                }
            }
        }
    }

    private fun loadSubFolder(id: Long) {
        lifecycleScope.launch {
            // 1) load data on IO thread
            withContext(Dispatchers.IO) {
                val folder = DBManager.get().folderDao().getWithData(id)
                selectedFolders.add(folder)
                // 2) update UI on main thread
                withContext(Dispatchers.Main) {
                    updateUI()
                }
            }
        }
    }

    private fun onDesktopSettingsChanged() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val desktops = DBManager.get().desktopDao().getAll()
                val desktopItems = desktops.map { DesktopItem(it) }
                selectedDesktop = selectedDesktop?.let { DBManager.get().desktopDao().getWithData(it.desktop.desktopId) }
                withContext(Dispatchers.Main) {
                    desktopItemAdapter.setNewList(desktopItems)
                    updateListBackground()
                }
            }
        }
    }

    private fun onFolderSettingsChanged() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                selectedDesktop = selectedDesktop?.let { DBManager.get().desktopDao().getWithData(it.desktop.desktopId) }
                selectedFolders = selectedFolders.map {
                    DBManager.get().folderDao().getWithData(it.folder.folderId)
                }.toMutableList()
                withContext(Dispatchers.Main) {
                    updateUI()
                }
            }
        }
    }


    // ------------
    // UI updates
    // ------------

    private fun initUI() {
        // 1) Desktop List
        desktopFastAdapter.apply {
            onClickListener = { view, adapter, item, position ->
                loadDesktop(item.desktopItem.desktopId)
                true
            }
            onLongClickListener = { view, adapter, item, position ->
                showSettingsAlertDialog(item.desktopItem)
                true
            }
        }
        binding.rvDesktops.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.rvDesktops.adapter = desktopFastAdapter

        // 2) Folder List
        folderFastAdapter.apply {
            onClickListener = { view, adapter, item, position ->
                loadSubFolder(item.folderItem.folderId)
                true
            }
            onLongClickListener = { view, adapter, item, position ->
                showSettingsAlertDialog(item.folderItem)
                true
            }
        }
        val span = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 8 else 4
        binding.rvFolders.layoutManager = GridLayoutManager(this, span, RecyclerView.VERTICAL, false)
        binding.rvFolders.adapter = folderFastAdapter

        // 3) Setting Button
        binding.btDesktopSettings.setOnClickListener {
            Toast.makeText(this, "Long press a desktop item to show and change its custom settings!", Toast.LENGTH_LONG).show()
        }

        binding.btFolderSettings.setOnClickListener {
            Toast.makeText(this, "Long press a folder item to show and change its custom settings!", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI() {
        updateList()
        updateListBackground()
        updateInfoViews()
    }

    private fun updateList() {
        // simple replace list function for demo purposes... will always be used even for state changes
        val folderItems = (selectedFolders.lastOrNull()?.subFolders
                ?: selectedDesktop?.folders)?.map { FolderItem(it) } ?: emptyList()

        folderItemAdapter.setNewList(folderItems)
    }

    private fun updateListBackground() {
        binding.rvFolders.setBackgroundColor(selectedDesktop?.desktop?.calcColor()
                ?: Color.TRANSPARENT)
    }

    private fun updateInfoViews() {
        val selectedFolder = selectedFolders.lastOrNull()
        if (selectedFolder != null) {
            binding.tvFoldersInfo.text = "${selectedDesktop!!.desktop.label} > ${selectedFolders.map { it.folder.label }.joinToString(" > ")}"
        } else if (selectedDesktop != null) {
            binding.tvFoldersInfo.text = selectedDesktop!!.desktop.label
        } else {
            binding.tvFoldersInfo.text = "No desktop selected - please click on a desktop to select one..."
        }
    }

    private fun showSettingsAlertDialog(item: Any) {
        if (item is DBFolderItem) {
            val customSettData = DBSettingsData(item.folderId)
            showSettingsDialog(customSettData, false, "Settings for ${item.label}")
        } else if (item is DBDesktopItem) {
            val customSettData = DBSettingsData(item.desktopId)
            showSettingsDialog(customSettData, true, "Settings for ${item.label}")
        } else {
            return
        }
    }

    private fun showSettingsDialog(settingsData: ISettingsData, desktop: Boolean, title: String) {

        val dialogBinding = DialogSettingsBinding.inflate(layoutInflater)

        val definitions = SettingsDefinition(
                (if (desktop) SettingsDefinitions.SETTINGS_FOR_DESKTOPS else SettingsDefinitions.SETTINGS_FOR_FOLDERS),
                emptyList() // no dependencies in this demo
        )

        val state = SettingsState() // default state... could be a restored state as well...
        val settings = Settings(definitions, settingsData, SETUP)
        dialogBinding.settingsView.bind(Settings.ViewContext.Activity(this), state, settings)

        AlertDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton("Back") { dlg, _ ->
                    dlg.dismiss()
                }
                .setView(dialogBinding.root)
                .create()
                .show()
    }
}