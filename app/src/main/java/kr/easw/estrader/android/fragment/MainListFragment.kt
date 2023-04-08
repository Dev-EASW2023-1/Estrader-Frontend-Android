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
        initialize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerBinding = null
    }

    // ViewHolder 에 사용할 DateList 초기화
    private fun initialize() {
        // 서버와 데이터 주고 받을 때 작업 중임을 알리는 ProgressDialog
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        // Volley Builder 패턴을 통한 네트워크 통신
        RestRequestTemplate.Builder<UserListDto>()
            .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
            .setRequestUrl("$SERVER_URL/user/test")
            .setRequestParams(UserListDto::class.java)
            .setRequestMethod(Request.Method.GET)
            .setListener{

                // 서버에서 받아온 Response 처리
                val dataList: MutableList<MainItem> = mutableListOf()

                for(x in 0..2){
                    dataList.add(MainItem(
                        it.userDto[x].picture,
                        it.userDto[x].period,
                        it.userDto[x].information,
                        it.userDto[x].location,
                        it.userDto[x].auctionperiod,
                        it.userDto[x].reserveprice
                    ))
                }

                initRecycler(dataList)

                // 서버 통신 완료 후 ProgressDialog 종료
                progressDialog.dismiss()
            }
            .build(requireContext())
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

                // Bundle 을 이용해 position 에 해당하는 이미지 URL 넘기기
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.framelayout, ItemLookUpFragment.indexImage(itemList[position].iconDrawable))
                }
            }
        })
    }
}