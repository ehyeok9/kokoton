package com.example.temp.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.temp.R
import com.example.temp.databinding.FragmentHomeBinding
import com.example.temp.history.TravelHistoryInterface
import com.example.temp.history.TravelHistoryRetrofitClient
import com.example.temp.history.models.TravelHistoryResponse
import com.example.temp.history.recycler.HistoryAdapter
import com.example.temp.history.recycler.HistoryData
import com.example.temp.tour_add.base_choice.BaseChoiceActivity
import kotlinx.android.synthetic.main.item_recom_card.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    lateinit var myTourListAdapter: MyTourListAdapter
    val  myTourListArray = mutableListOf<MyTourListModel>()


//    var myTourListArray: ArrayList<MyTourListModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.viewFlipper.flipInterval = 3000  //화면 넘김 간격 메서드 (millisceond)
        binding.viewFlipper.startFlipping()

//        val myTourListArray = arrayListOf<MyTourListModel>(
////            MyTourListModel("프랑스","2023.04.21~2023.05.14"),
////            MyTourListModel("일본","2023.01.21~2023.01.30"),
////            MyTourListModel("베트남","2023.03.11~2023.03.22"),
////            MyTourListModel("미국","2023.01.11~2023.01.14"),
//
//        )
        getTravelHistoryData()

        if(myTourListArray.isNullOrEmpty()){
            //비어있다면
            binding.notMyHomeList.visibility = View.VISIBLE
            binding.myHomeList.visibility = View.GONE

        }else{
            binding.notMyHomeList.visibility = View.GONE
            binding.myHomeList.visibility = View.VISIBLE
        }

//        binding.myHomeList.layoutManager = LinearLayoutManager(requireContext())
//        binding.myHomeList.adapter = MyTourListAdapter(requireContext(),myTourListArray)
        var today = Calendar.getInstance()
        Log.i("test","오늘의 날짜 : ${today}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addTourBtn.setOnClickListener{
            val intent = Intent(context, BaseChoiceActivity::class.java)
            startActivity(intent)
        }

    }


    private fun getTravelHistoryData(){
        val travelHistoryInterface = TravelHistoryRetrofitClient.sRetrofit.create(
            TravelHistoryInterface::class.java)
        travelHistoryInterface.getTravelHistoryList().enqueue(object :
            Callback<TravelHistoryResponse> {
            override fun onResponse(
                call: Call<TravelHistoryResponse>,
                response: Response<TravelHistoryResponse>
            ) {
                if(response.isSuccessful){
                    val result = response.body() as TravelHistoryResponse

                    myTourListAdapter = MyTourListAdapter(requireActivity())
                    binding.myHomeList.adapter = myTourListAdapter

                    for(i in result.result.indices){
                        myTourListArray.apply { add(MyTourListModel(
                            country = result.result[i].country,
                            sDate = result.result[i].startDate,
                            eDate = result.result[i].endDate)
                        ) }
                    }

                    myTourListAdapter.myTourListArray = myTourListArray
                    myTourListAdapter.notifyDataSetChanged()
                    if(myTourListArray.isNullOrEmpty()){
                        //비어있다면
                        binding.notMyHomeList.visibility = View.VISIBLE
                        binding.myHomeList.visibility = View.GONE

                    }else{
                        binding.notMyHomeList.visibility = View.GONE
                        binding.myHomeList.visibility = View.VISIBLE
                    }


                }else{
                    Log.d("TravelHistory", "onResponse: Error code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TravelHistoryResponse>, t: Throwable) {
                Log.d("TravelHistory", t.message ?: "통신오류")
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}