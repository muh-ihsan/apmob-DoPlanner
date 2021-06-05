package com.tubesApmob.doplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.tubesApmob.doplanner.databinding.DplForgotPasswordFragmentBinding

class ForgotPasswordFragment : Fragment(R.layout.dpl_forgot_password_fragment) {
    private var _binding: DplForgotPasswordFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DplForgotPasswordFragmentBinding.inflate(inflater, container, false)

        binding.btCancelForgotPass.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}