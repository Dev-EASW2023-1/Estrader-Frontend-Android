package kr.easw.estrader.android.fragment.realtor


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.FragmentEnterPriceBinding
import kr.easw.estrader.android.databinding.FragmentProxyselBinding
import kr.easw.estrader.android.fragment.delegation.user.SuccessDelegationFragment
import kr.easw.estrader.android.fragment.user.ProxyselFragment
import java.text.NumberFormat
import java.util.Locale

class EnterPriceFragment : Fragment() {
    private var _binding: FragmentEnterPriceBinding? = null
    private val binding get() = _binding!!
    private var price = ""
    private lateinit var buttons: Array<Button>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnterPriceBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun updateTextField(input: String) {
        val currentText =
            binding.price.text.toString().replace("원", "").replace(",", "").replace(" ", "")

        val newText = when (input) {
            "del" -> {
                if (currentText.isNotEmpty()) currentText.dropLast(1) else ""
            }

            else -> {
                currentText + input
            }
        }

        if (newText.isNotEmpty()) {
            val number = newText.toLong()
            val formattedNumber = NumberFormat.getNumberInstance(Locale.KOREA).format(number)
            binding.price.text = "${formattedNumber}원"
            price = number.toString()
            val koreanFormat = convertWon(number)
            binding.priceKorea.text = koreanFormat
        } else {
            binding.price.text = ""
            binding.priceKorea.text = ""
        }
    }

    private fun convertWon(number: Long): String {
        if (number < 10000) {
            return "$number 원"
        }
        if (number > 10000000000000) {
            return "값이 너무 많습니다."
        }
        val eok = number / 100000000
        val man = (number % 100000000) / 10000
        val chun = (number % 10000) / 1000
        val baek = (number % 1000) / 100

        return buildString {
            if (eok > 0) append("${eok}억")
            if (man > 0) append("${man}만")
            if (chun > 0) append(" ${chun}천")
            if (baek > 0) append(" ${baek}백")
            append("원")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.next.setOnClickListener {
            val realtorInfoFragment = ProxyselFragment()

            parentFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragment_container,
                    realtorInfoFragment
                )
                addToBackStack(null)
                commit()
            }


        }


        buttons = arrayOf(
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9,
            binding.button0,
            binding.button00,
            binding.button0000
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                val input = when (button.id) {
                    R.id.button1 -> "1"
                    R.id.button2 -> "2"
                    R.id.button3 -> "3"
                    R.id.button4 -> "4"
                    R.id.button5 -> "5"
                    R.id.button6 -> "6"
                    R.id.button7 -> "7"
                    R.id.button8 -> "8"
                    R.id.button9 -> "9"
                    R.id.button0 -> "0"
                    R.id.button00 -> "00"
                    R.id.button0000 -> "del"
                    else -> ""
                }
                updateTextField(input)
            }
        }
    }
}