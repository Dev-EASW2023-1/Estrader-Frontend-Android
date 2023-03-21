package kr.easw.estrader.android.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.R

class AgentdelegationFragment : Fragment()  {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_agentdelegation, container, false)

        // accept 버튼 클릭 시 DialogActivity 실행
        val acceptButton = view.findViewById<Button>(R.id.confirm_button2)
        acceptButton.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.fragment_dialog)
            val button = dialog.findViewById<Button>(R.id.positive_button)
            val button2 = dialog.findViewById<Button>(R.id.negative_button)

            button.setOnClickListener {
                Toast.makeText(requireContext(), "positive_button이 클릭되었습니다!", Toast.LENGTH_SHORT).show()
            }
            button2.setOnClickListener {
                Toast.makeText(requireContext(), "negative_button이 클릭되었습니다!", Toast.LENGTH_SHORT).show()
            }

            dialog.show()
        }

        val negativeButton = view.findViewById<Button>(R.id.confirm_button)
        negativeButton.setOnClickListener {
            Toast.makeText(requireContext(), "negative_button이 클릭되었습니다!", Toast.LENGTH_SHORT).show()
        }

        return view
    }

}

