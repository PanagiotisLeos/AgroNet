package com.example.agronet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class FarmerDetailFragment : Fragment() {

    private lateinit var farmer: Farmer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            farmer = it.getSerializable("farmer") as Farmer
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set up views to display farmer details
        return inflater.inflate(R.layout.farmer_profile_from_user, container, false)
    }

    companion object {
        fun newInstance(farmer: Farmer): FarmerDetailFragment {
            val fragment = FarmerDetailFragment()
            val args = Bundle()
            args.putParcelable("farmer", farmer)
            fragment.arguments = args
            return fragment
        }
    }
}
