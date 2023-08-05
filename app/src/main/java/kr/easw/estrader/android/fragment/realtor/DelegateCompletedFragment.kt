package kr.easw.estrader.android.fragment.realtor

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context.DOWNLOAD_SERVICE
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.easw.estrader.android.databinding.ElementRealtorDelegationBinding
import kr.easw.estrader.android.databinding.FragmentDelegateCompletedlistBinding
import kr.easw.estrader.android.definitions.PDF_URL
import kr.easw.estrader.android.fragment.BaseFragment
import kr.easw.estrader.android.model.data.DelegateCompletionHolder
import kr.easw.estrader.android.model.dto.DelegateCompletionItem
import kr.easw.estrader.android.util.PDFUtil
import java.io.File
import java.lang.ref.WeakReference


/**
 * 대리인 전용 메인화면 Fragment
 * 대리위임 완료 목록
 *
 * 사용 미정
 */
class DelegateCompletedFragment : BaseFragment<FragmentDelegateCompletedlistBinding>(FragmentDelegateCompletedlistBinding::inflate){

    private var dataList: MutableList<DelegateCompletionItem>? = null
    private var itemClickListener: WeakReference<OnItemClickListener>? = null
    private var recyclerBinding: ElementRealtorDelegationBinding? = null
    private var downloadReceiver : BroadcastReceiver? = null
    private var downloadCompleteFilter : IntentFilter? = null
    private var downloadManager : DownloadManager? = null
    private var isReceiverRegistered = false
    private var file : File? = null
    private var uri : Uri? = null

    private val downloadButton: Button by lazy {
        recyclerBinding!!.auctiondownload
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        initializeData()
        initRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerBinding = null
    }

    // ViewHolder 에 사용할 DateList 초기화
    private fun initializeData() {
        dataList = mutableListOf(
            DelegateCompletionItem(
                "남재경",
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27\n ~ \n04-07"
            ), DelegateCompletionItem(
                "김성준",
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27\n ~ \n04-07"
            ),
            DelegateCompletionItem(
                "엄선용",
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27\n ~ \n04-07"
            ),
            DelegateCompletionItem(
                "허석무",
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27\n ~ \n04-07"
            ),
            DelegateCompletionItem(
                "최이루",
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27\n ~ \n04-07"
            ),
            DelegateCompletionItem(
                "임정수",
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27\n ~ \n04-07"
            )
        )
    }

    private fun initRecycler() {
        val recyclerViewAdapter = object : RecyclerView.Adapter<DelegateCompletionHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): DelegateCompletionHolder {
                recyclerBinding = ElementRealtorDelegationBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                downloadButton
                return DelegateCompletionHolder(recyclerBinding, itemClickListener?.get())
            }

            override fun onBindViewHolder(
                holder: DelegateCompletionHolder, position: Int
            ) {
                holder.bind(dataList!![position])
            }

            override fun getItemCount(): Int = dataList!!.size

            // 리스너 객체 참조를 recyclerView Adapter 에 전달
            // 약한 참조를 위해 WeakReference 설정
            fun setOnItemClickListener(listener: OnItemClickListener) {
                itemClickListener = WeakReference(listener)
            }
        }

        (binding as FragmentDelegateCompletedlistBinding).delegatecompletionRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerViewAdapter
        }

        // recyclerView 아이템 클릭 이벤트 설정
        recyclerViewAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                file = PDFUtil.createSharedFile()
                file?.let {
                    uri = PDFUtil.createUri(requireActivity(), it)
                    urlDownloading(file)
                }
            }
        })
    }

    private fun urlDownloading(file: File?) {
        file?.let {
            // DownloadManager 초기화
            downloadManager = requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager?.let { manager ->
                val request = DownloadManager.Request(Uri.parse(PDF_URL))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setRequiresCharging(false)
                    .setDestinationUri(Uri.fromFile(it))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                val downloadId = manager.enqueue(request)

                if(!isReceiverRegistered) {
                    // Download 완료를 알려줄 BroadcastReceiver 생성
                    downloadReceiver = PDFUtil.downloadReceiver(downloadId, manager)
                    downloadCompleteFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                    requireActivity().registerReceiver(downloadReceiver, downloadCompleteFilter)
                    isReceiverRegistered = true
                }
            }
        }
    }

    // 다른 Activity 호출 시 BroadcastReceiver 등록 취소
    override fun onPause() {
        super.onPause()
        if(isReceiverRegistered) {
            requireActivity().unregisterReceiver(downloadReceiver)
            downloadReceiver = null
            isReceiverRegistered = false
        }
    }

}