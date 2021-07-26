/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.handlUser.app.R
import com.handlUser.app.databinding.AdapterHomeBinding
import com.handlUser.app.model.CategoryData
import com.handlUser.app.model.SubCategory
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.loadForCate
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.services.*
import com.handlUser.app.utils.Const

class AdapterServiceHome(val baseActivity: BaseActivity, val list: ArrayList<SubCategory>) : BaseAdapter() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterHomeBinding>(LayoutInflater.from(parent.context), R.layout.adapter_home, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterHomeBinding
        binding.data = list[position]
        binding.userIV.loadForCate(baseActivity, list[position].image)
        binding.root.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("data", list[position])
            when (list[position].typeId) {
                Const.WALL_PAINT -> {
                    baseActivity.replaceFragment(ServiceWallPaintingFragment(), bundle)
                }
                Const.GARDEN_SERVICE -> {
                    baseActivity.replaceFragment(ServiceGardenFragment(), bundle)
                }
                Const.FURNITURE_ASSEMBLING -> {
                    baseActivity.replaceFragment(ServiceFurnitureAssembleFragment(), bundle)
                }
                Const.NANNY -> {
                    baseActivity.replaceFragment(ServiceNannyFragment(), bundle)
                }
                Const.SCHOOL_TUTOR -> {
                    baseActivity.replaceFragment(ServiceTutorFragment(), bundle)
                }
                Const.PET_WALKING -> {
                    baseActivity.replaceFragment(ServicePetWSFragment(), bundle)
                }
                Const.NUTRITION -> {
                    baseActivity.replaceFragment(ServiceNutritionistFragment(), bundle)
                }
                Const.MAKEUP -> {
                    baseActivity.replaceFragment(ServiceArtistFragment(), bundle)
                }
                Const.HOME_CLEANING -> {
                    baseActivity.replaceFragment(ServiceHomeCleaningFragment(), bundle)
                }
                Const.MATTRESS_CLEANING -> {
                    baseActivity.replaceFragment(ServiceMCSCleaningFragment(), bundle)
                }
                Const.AC_CLEANING -> {
                    baseActivity.replaceFragment(ServiceAcCleaningFragment(), bundle)
                }
                Const.PEST_CONTROLL -> {
                    baseActivity.replaceFragment(ServicePestControlFragment(), bundle)
                }
                Const.CAR_WASH -> {
                    baseActivity.replaceFragment(ServiceCarWashFragment(), bundle)
                }
                Const.DISINFECTION -> {
                    baseActivity.replaceFragment(ServiceDisinfectionFragment(), bundle)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}