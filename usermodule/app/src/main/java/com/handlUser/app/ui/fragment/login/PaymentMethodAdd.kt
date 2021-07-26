package com.handlUser.app.ui.fragment.login

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentPaymentListBinding
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.model.CardModel
import com.handlUser.app.ui.activity.LoginSignUpActivity
import com.handlUser.app.ui.adapter.AdapterPaymentList
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.extensions.replaceFragmentWithoutStackWithoutBundle
import com.handlUser.app.ui.fragment.AddPaymentMethodFragment
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.ui.fragment.HomeFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.handlUser.app.utils.SwipeHelper
import com.handlUser.app.utils.SwipeHelperDelete
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject

class PaymentMethodAdd  : BaseFragment(), ClickHandler {

    private var binding: FragmentPaymentListBinding? = null
    private var list = ArrayList<CardModel>()
    private var adapter: AdapterPaymentList? = null

    private var data: AppointmentData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            data = arguments?.getParcelable<AppointmentData>("booking_data")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_payment_list, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        list.clear()
        adapter = null
        binding!!.handleClick = this
        if (baseActivity is LoginSignUpActivity) {
            binding!!.dotsI.dot4.setImageResource(R.drawable.circle_dark_blue)
            binding!!.logoIV.visibility = View.VISIBLE
        } else {
            binding!!.logoIV.visibility = View.GONE
        }
        baseActivity!!.setToolbar(title = getString(R.string.payment_methods))
        hitCardListAPI()
        setUpRecyclerView()
        if (data == null) {
            setHasOptionsMenu(true)
        }

        binding!!.pullToRefresh.setOnRefreshListener {
            list.clear()
            adapter = null
            hitCardListAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }

    }


    private fun hitCardListAPI() {
        val call = api!!.apiCardList()
        restFullClient!!.sendRequest(call, this)

    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = if (data != null) {
                AdapterPaymentList(baseActivity!!,  list, true)
            } else {
                AdapterPaymentList(baseActivity!!,  list, false)
            }
            binding!!.addressRV.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    var deletePos = -1
    private fun setUpRecyclerView() {


        val swipeHelper: SwipeHelperDelete = object : SwipeHelperDelete(baseActivity!!, binding!!.addressRV) {
            override fun instantiateUnderlayButton(viewHolder: RecyclerView.ViewHolder?, underlayButtons: MutableList<UnderlayButton?>) {
                underlayButtons.add(UnderlayButton(resources.getString(R.string.delete),0, Color.parseColor("#ffff4444"),
                        UnderlayButtonClickListener {
                            deletePos = viewHolder!!.adapterPosition
                            hitApiDeleteCard(list[viewHolder!!.adapterPosition].id)
                        }, baseActivity!!))
            }
        }
    }

    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
                baseActivity!!,
                baseActivity!!.getString(R.string.delete),
                14.0f,
                android.R.color.holo_red_light,
                object : SwipeHelper.UnderlayButtonClickListener {
                    override fun onClick() {
                        hitApiDeleteCard(list[position].id)
                    }
                })
    }

    private fun hitApiDeleteCard(userId: String) {
        val call = api!!.apiCardDelete(userId)
        restFullClient!!.sendRequest(call, this)

    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.addRL, R.id.addTV -> baseActivity!!.replaceFragment(AddPaymentMethodFragment())
            R.id.doneTV -> if (list.size > 0) {
                if (data != null) {
                    val selectList = list.filter { it.isSelected }

                    if (selectList.isEmpty()) {
                        showToastOne(getString(R.string.select_card))
                    } else {
                        hitTransactionApi(selectList)
                    }
                } else {
                    baseActivity!!.replaceFragment(AddUserDetailFragment())
                }
            } else {
                showToastOne(getString(R.string.please_add_payment_method))
            }

        }
    }

    private fun hitTransactionApi(selectList: List<CardModel>) {
        val param = Api3Params()
        param.put("model_id", data!!.id)
        param.put("price", data!!.price)
        val call = api!!.hitOrderPayment(param.getServerHashMap(), selectList[0].id)
        restFullClient!!.sendRequest(call, this)

    }


    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_CARD_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    val jsonArray = jsonobject.getJSONArray("list")
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<CardModel>(object1.toString(), CardModel::class.java)
                        list.add(data)

                    }
                    if (list.size > 0) {
                        setAdapter()
                        binding!!.doneTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.light_blue_button)
                    } else {
                        binding!!.doneTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.grey_button)

                    }

                }
            } else if (responseUrl.contains(Const.API_CARD_DELETE)) {
                if (responseCode == Const.STATUS_OK) {
                    if (jsonobject.has("message")){
                        showToast(jsonobject.getString("message"))
                    }

                    list.removeAt(deletePos)
                    adapter!!.notifyDataSetChanged()
                    if (list.size > 0) {
                        setAdapter()
                        binding!!.doneTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.light_blue_button)
                    } else {
                        binding!!.doneTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.grey_button)

                    }

                }
            } else if (responseUrl.contains(Const.API_PAYMENT_ORDER)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(jsonobject.optString("message"))

                    baseActivity!!.replaceFragmentWithoutStackWithoutBundle(HomeFragment())

                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_logout, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutMB -> {
                baseActivity!!.logOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
