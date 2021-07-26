/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment.login

import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.handlService.app.R
import com.handlService.app.databinding.FragmentIcardInstructionBinding
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const


class ICardInstructionFragment : BaseFragment(), ClickHandler, BaseActivity.PermCallback {

    private var binding: FragmentIcardInstructionBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_icard_instruction, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(baseActivity!!.getString(R.string.welcom))
        binding!!.passwordTagTV.text= Html.fromHtml(baseActivity!!.getString(R.string.please_take_pic))

    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.takephotoTV -> {
                if (baseActivity!!.checkPermissions(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), Const.IMAGE_PERMISSION, this)) {
                    chooseImagePicker(Const.IMAGE_PERMISSION, false)
                }

            }

        }
    }

    override fun permGranted(resultCode: Int) {
        chooseImagePicker(Const.IMAGE_PERMISSION, false)

    }

    override fun permDenied(resultCode: Int) {


    }

    override fun onImageSelected(uri: Uri?, requestCode: Int) {
        super.onImageSelected(uri, requestCode)
        if (requestCode == Const.IMAGE_PERMISSION) {
            val bundle = Bundle()
            bundle.putBoolean("is_card", true)
            bundle.putString("file", uri!!.toString())
            baseActivity!!.replaceFragment(ICardDetailFragment(), bundle)
        }
    }
}
