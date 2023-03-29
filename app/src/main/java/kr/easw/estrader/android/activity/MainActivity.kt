package kr.easw.estrader.android.activity

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityLoginBinding
import kr.easw.estrader.android.fragment.LoginFragment
import kr.easw.estrader.android.fragment.RegisterFragment

/**
 * 로그인, 회원 가입 activity
 * PDF 저장과 FCM에 대한 권한 요청
 * 상단 탭에서 로그인 (LoginFragment), 회원가입 (RegisterFragment) 이동
 * LOGIN 버튼을 누르면 MainListActivity 로 이동
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var deniedList: List<String>

    private val signInTextView: TextView by lazy {
        binding.signIn
    }
    private val signUpTextView: TextView by lazy {
        binding.signUp
    }

    companion object {
        const val requestFinal = 444

        // android 11에서 기존 권한 (android.permission.WRITE_EXTERNAL_STORAGE) 무시
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFields()

        // 로그인 Fragment init
        initFragment()

        // 권한 요청
        permissionRequest()

        //로그인 Textview 클릭 이벤트
        signInTextView.setOnClickListener {
            signInClick()
        }

        //회원 가입 Textview 클릭 이벤트
        signUpTextView.setOnClickListener {
            signUpClick()
        }
    }

    private fun initFields(){
        signInTextView
        signUpTextView
    }

    private fun initFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_view, LoginFragment())
            .commit()
    }

    //로그인 Textview 클릭 이벤트
    private fun signInClick() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.container_view, LoginFragment())
            .commit()
    }

    //회원 가입 Textview 클릭 이벤트
    private fun signUpClick() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.container_view, RegisterFragment())
            .commit()
    }

    private fun permissionRequest() {
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            // 거부된 권한 목록 저장
            deniedList = result.filter {
                !it.value
            }.map {
                it.key
            }

            // 거부된 권한 목록 남아 있으면 권한 재요청
            when {
                deniedList.isNotEmpty() -> {
                    requestAgain(deniedList.toTypedArray())
                }
                else -> {
                    Snackbar.make(binding.root, "All request are permitted", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        resultLauncher.launch(permissions)
    }

    // requestCode 보내고 권한 재요청
    private fun requestAgain(its: Array<String>) {
        AlertDialog.Builder(this)
            .setTitle("권한 재요청")
            .setMessage("권한이 필요합니다. 권한 요청을 다시 수락하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    its,
                    requestFinal
                )
            }
            .setNegativeButton("취소") { _, _ ->
                Snackbar.make(binding.root, "권한 재요청을 취소하셨습니다.", Snackbar.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    //requestCode 받고 권한 요청에 대한 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestFinal) {

            // 또 권한 거부 시 거부된 권한 목록 저장
            val permission = permissions.toList().filter {
                ActivityCompat.checkSelfPermission(
                    this,
                    it!!
                ) == PackageManager.PERMISSION_DENIED
            }

            // 메시지 출력
            if (permission.isNotEmpty()){
                Snackbar.make(binding.root, "권한 재요청을 취소하셨습니다.", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, "All request are permitted", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}