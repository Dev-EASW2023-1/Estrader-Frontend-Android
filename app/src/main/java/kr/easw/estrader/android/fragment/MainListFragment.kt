package kr.easw.estrader.android.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ElementItemlistBinding
import kr.easw.estrader.android.databinding.FragmentMainlistBinding
import kr.easw.estrader.android.definitions.SERVER_URL
import kr.easw.estrader.android.model.data.MainHolder
import kr.easw.estrader.android.model.dto.MainItem
import kr.easw.estrader.android.model.dto.UserListDto
import kr.easw.estrader.android.util.RestRequestTemplate
import java.lang.ref.WeakReference

/**
 * 사용자 전용 부동산 매각정보 리스트 Fragment
 * 리스트 항목을 누르면 ItemLookUpFragment 로 이동
 */
class MainListFragment : BaseFragment<FragmentMainlistBinding>(FragmentMainlistBinding::inflate){

    private var itemClickListener: WeakReference<OnItemClickListener>? = null
    private var recyclerBinding: ElementItemlistBinding? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        initializeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerBinding = null
    }

    // ViewHolder 에 사용할 DateList 초기화
    private fun initializeData() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        RestRequestTemplate.Builder()
            .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
            .setRequestParams(UserListDto::class)
            .setRequestUrl("$SERVER_URL/user/test")
            .setRequestMethod(Request.Method.GET)
            .setListener{
                val dataList: MutableList<MainItem> = mutableListOf()

                println("X3 | ${it.get("userDto")}")
                println("X4 | ${it.getAsJsonArray("userDto")}")
                println("X5 | ${it.getAsJsonArray("userDto").get(1)}")
                println("X6 | ${it.getAsJsonArray("userDto").get(1).asJsonObject}")
                println("X7 | ${it.getAsJsonArray("userDto").get(1).asJsonObject.get("picture")}")

                dataList.add(MainItem(
                    removeDot(it.getAsJsonArray("userDto").get(0).asJsonObject.get("picture").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(0).asJsonObject.get("period").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(0).asJsonObject.get("information").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(0).asJsonObject.get("location").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(0).asJsonObject.get("auctionperiod").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(0).asJsonObject.get("reserveprice").asString.replace("\\n", ""))
                ))
                dataList.add(MainItem(
                    removeDot(it.getAsJsonArray("userDto").get(1).asJsonObject.get("picture").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(1).asJsonObject.get("period").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(1).asJsonObject.get("information").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(1).asJsonObject.get("location").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(1).asJsonObject.get("auctionperiod").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(1).asJsonObject.get("reserveprice").asString.replace("\\n", ""))
                ))
                dataList.add(MainItem(
                    removeDot(it.getAsJsonArray("userDto").get(2).asJsonObject.get("picture").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(2).asJsonObject.get("period").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(2).asJsonObject.get("information").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(2).asJsonObject.get("location").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(2).asJsonObject.get("auctionperiod").toString()),
                    removeDot(it.getAsJsonArray("userDto").get(2).asJsonObject.get("reserveprice").asString.replace("\\n", ""))
                ))

                initRecycler(dataList)

                progressDialog.dismiss()
            }
            .build(requireContext())
    }

    private fun removeDot(str : String ) : String {
        val re = "^\"|\"$".toRegex()
        return str.replace(re, "")
    }

    private fun initRecycler(itemList: MutableList<MainItem>) {
        val recyclerAdapter = object : RecyclerView.Adapter<MainHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): MainHolder {
                recyclerBinding = ElementItemlistBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return MainHolder(recyclerBinding, itemClickListener?.get())
            }

            override fun onBindViewHolder(
                holder: MainHolder, position: Int
            ) {
                holder.bind(itemList[position])
            }

            override fun getItemCount(): Int = itemList.size

            // 리스너 객체 참조를 recyclerView Adapter 에 전달
            // 약한 참조를 위해 WeakReference 설정
            fun setOnItemClickListener(listener: OnItemClickListener) {
                itemClickListener = WeakReference(listener)
            }
        }

        (binding as FragmentMainlistBinding).mainlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
        }

        // recyclerView 아이템 클릭 이벤트 설정
        recyclerAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val itemLookUpFragment = ItemLookUpFragment.indexnum(position)
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.framelayout, itemLookUpFragment)

                }
            }
        })
    }
}