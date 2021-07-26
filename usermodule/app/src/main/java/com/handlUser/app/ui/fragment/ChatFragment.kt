/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.handlUser.app.model.ChatData
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentChatBinding
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.adapter.ChatAdapter
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.extensions.isBlank
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3MultipartByte
import com.toxsl.restfulClient.api.extension.handleException
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import org.json.JSONObject
import toxsl.imagebottompicker.ImageBottomPicker
import java.io.File


class ChatFragment : BaseFragment(), ClickHandler, BaseActivity.PermCallback {

    private var binding: FragmentChatBinding? = null
    private var toId: String? = null
    private var toName: String? = null
    private var isFileProfile: File? = null
    private val messageList: ArrayList<ChatData> = ArrayList()
    private val task = Runnable { this.getUpdatedMessage() }
    private var handler: Handler? = null
    private val iTimerIn2erval: Long = 3000
    private var adapter: ChatAdapter? = null
    private var isSingle = false
    private var pageCount = 0
    private var type = -1
    private var emojiPopup: EmojiPopup? = null
    private var modelId: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_chat, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    //get chat id and name
    private fun getBundleData() {
        if (arguments != null) {
            toId = arguments?.getString("to_id", "")
            modelId = arguments?.getString("model_id", "")
            toName = arguments?.getString("to_name", "")
            store!!.saveString("chat_id", toId!!)
        }
    }

    private fun initUI() {
        getBundleData()
        binding!!.handleClick = this
        baseActivity?.setToolbar(toName)
        EmojiManager.install(GoogleEmojiProvider())
        emojiPopup = EmojiPopup.Builder.fromRootView(binding!!.root).build(binding!!.messageET)
        val linearLayoutManager = LinearLayoutManager(baseActivity!!)
        linearLayoutManager.stackFromEnd = true // want to reverse the coming array
        linearLayoutManager.reverseLayout = true
        binding!!.notifyRV.layoutManager = linearLayoutManager

        (binding!!.notifyRV.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        getPreviousMessages()

    }


    private fun validate(): Boolean {
        if (binding!!.messageET.isBlank()) {
            baseActivity!!.showToastOne(baseActivity!!.getString(R.string.write_message))
        } else {
            return true
        }
        return false
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.sendIV -> {
                if (validate()) {
                    type = 0
                    hitSendMessageApi()
                }
                binding!!.messageET.setText("")
            }
            R.id.emoijiIV -> {
                emojiPopup!!.toggle()
                binding!!.cameraIV.visibility = View.GONE
                binding!!.emoijiIV.visibility = View.GONE
                binding!!.keyboardIV.visibility = View.VISIBLE
                binding!!.emoijiIV.setImageResource(R.drawable.ic_baseline_keyboard_24)
            }
            R.id.keyboardIV -> {
                emojiPopup!!.toggle()
                binding!!.cameraIV.visibility = View.VISIBLE
                binding!!.emoijiIV.visibility = View.VISIBLE
                binding!!.keyboardIV.visibility = View.GONE
                binding!!.emoijiIV.setImageResource(R.drawable.ic_emoticon)
            }
            R.id.cameraIV -> {
                type = 1
                selectProfileImage()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        handler!!.postDelayed(task, iTimerIn2erval)
    }


    override fun onStop() {
        handler!!.removeCallbacks(task)
        handler = null
        super.onStop()
    }

    override fun onSyncStart() {}

    override fun onPause() {
        super.onPause()
        baseActivity!!.store!!.saveString("chat_id", "0")
    }

    private fun hitSendMessageApi() {
        val param = Api3MultipartByte()
        if (type == Const.STATE_IMAGE) {
            try {
                param.put("ChatMessage[message]", isFileProfile)
            } catch (e: Exception) {
                handleException(e)
            }

        } else {
            param.put("ChatMessage[message]", binding!!.messageET.checkString())
        }
        param.put("ChatMessage[state_id]", type.toString())
        param.put("ChatMessage[user_id]", toId.toString())
        param.put("ChatMessage[created_by_id]", baseActivity!!.getProfileData()?.id.toString())
        param.put("ChatMessage[model_id]", modelId ?: "")

        val call = api!!.apiSendMessage(param.getRequestBody())
        restFullClient!!.sendRequest(call, this)

    }

    //get new message
    private fun getUpdatedMessage() {
        val call = api!!.apiGetNewMessages(toId)
        restFullClient!!.sendRequest(call, this)

    }

    //get old messages
    fun getPreviousMessages() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetOldMessages(toId, pageCount, modelId)
            restFullClient!!.sendRequest(call, this)
        }

    }

    private fun setAdapter() {
        if (messageList.size > 0) {
            if (adapter == null) {
                adapter = ChatAdapter(baseActivity!!, messageList, this)
                binding!!.notifyRV.adapter = adapter
            } else {
                adapter!!.notifyDataSetChanged()
            }

        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            if (responseCode == Const.STATUS_OK) {
                val jsonObject = JSONObject(response!!)
                if (responseUrl.contains(Const.API_SEND_MESSAGE)) {
                    if (responseCode == Const.STATUS_OK) {
                        val data = Gson().fromJson(jsonObject.getJSONObject("detail").toString(), ChatData::class.java)
                        messageList.add(0, data)
                        setAdapter()
                        if (adapter != null) {
                            Handler(Looper.getMainLooper()).postDelayed({ binding!!.notifyRV.smoothScrollToPosition(0) }, 2)
                        }

                    }
                } else if (responseUrl.contains(Const.API_OLD_MESSAGES)) {
                    if (responseCode == Const.STATUS_OK) {
                        pageCount++
                        val metaObject = jsonObject.getJSONObject("_meta")
                        val totalId = metaObject.getInt("pageCount")
                        isSingle = pageCount >= totalId
                        if (jsonObject.has("list")) {
                            if (jsonObject.getJSONArray("list").length() > 0) {
                                for (k in 0 until jsonObject.getJSONArray("list").length()) {
                                    val data = Gson().fromJson(jsonObject.getJSONArray("list").getJSONObject(k).toString(), ChatData::class.java)
                                    messageList.add(data)
                                }
                            }
                            setAdapter()
                        }
                    } else {
                        isSingle = false
                    }
                } else if (responseUrl.contains(Const.API_NEW_MESSAGES)) {
                    if (handler != null) {
                        handler!!.postDelayed(task, iTimerIn2erval)
                    }
                    if (responseCode == Const.STATUS_OK) {
                        if (jsonObject.has("list")) {
                            for (k in 0 until jsonObject.getJSONArray("list").length()) {
                                val data = Gson().fromJson(jsonObject.getJSONArray("list").getJSONObject(k).toString(), ChatData::class.java)
                                messageList.add(0, data)
                            }
                            setAdapter()
                            if (adapter != null) {
                                Handler(Looper.getMainLooper()).postDelayed({ binding!!.notifyRV.smoothScrollToPosition(0) }, 2)
                            }

                        }
                    }
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override fun onResume() {
        super.onResume()
        baseActivity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        baseActivity!!.window.clearFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onImageSelected(uri: Uri?, requestCode: Int) {
        if (requestCode == Const.IMAGE_CODE1) {
            isFileProfile = File(uri!!.path!!)
            hitSendMessageApi()
        }
    }

    override fun permGranted(resultCode: Int) {
        if (resultCode == Const.IMAGE_CODE1) {
            selectProfileImage()
        }

    }

    private fun selectProfileImage() {

        if (baseActivity!!.checkPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), Const.IMAGE_CODE1, this)) {
            if (baseActivity!!.checkPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), Const.IMAGE_CODE1, this)) {
                val bottomPicker = ImageBottomPicker.Builder(baseActivity!!, Const.IMAGE_CODE1)
                        .setOnImageSelectedListener(this)
                        .showRemoved(false)
                        .create()
                bottomPicker.show(baseActivity?.supportFragmentManager)

            }

        }
    }

    override fun permDenied(resultCode: Int) {


    }
}
