package com.tubesApmob.doplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.tubesApmob.doplanner.databinding.DplLoginFragmentBinding

class LoginFragment : Fragment() {
    private var _binding: DplLoginFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DplLoginFragmentBinding.inflate(inflater, container, false)

        binding.btLoginfragmentRegister.setOnClickListener{
            requireView().findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.btLoginfragmentForgotpass.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}