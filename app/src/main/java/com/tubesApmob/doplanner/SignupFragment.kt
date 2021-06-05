package com.tubesApmob.doplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.tubesApmob.doplanner.databinding.DplSignupFragmentBinding

class SignupFragment : Fragment() {
    private var _binding: DplSignupFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DplSignupFragmentBinding.inflate(layoutInflater, container, false)

        binding.btCancelSignup.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}