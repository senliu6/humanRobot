package com.shciri.rosapp.ui.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.shciri.rosapp.R
import com.shciri.rosapp.databinding.FragmentUserManagerBinding
import com.shciri.rosapp.dmros.data.User
import com.shciri.rosapp.dmros.data.UserList.users
import com.shciri.rosapp.dmros.tool.UserRepository
import com.shciri.rosapp.dmros.tool.UserRepository.addUser
import com.shciri.rosapp.dmros.tool.UserRepository.changePassword
import com.shciri.rosapp.dmros.tool.UserRepository.getAllUsers
import com.shciri.rosapp.ui.user.adapter.UserAdapter
import com.shciri.rosapp.ui.dialog.InputDialog

/**
 * 功能：
 * @author ：liudz
 * 日期：2023年12月05日
 */
class UserManagerFragment : Fragment(), UserAdapter.OnItemClickListener {

    private lateinit var binding: FragmentUserManagerBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: MutableList<User>
    private lateinit var passwordDialog: InputDialog
    private lateinit var addDialog: InputDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserManagerBinding.inflate(layoutInflater, container, false)
        initView()
        initData()

        return binding.root


    }

    private fun initData() {

        for (user in users) {
            addUser(user)
        }

        binding.recyclerUser.layoutManager = LinearLayoutManager(this.context)
        userList = getAllUsers()
        userAdapter = UserAdapter(userList, this)
        binding.recyclerUser.adapter = userAdapter;



        addDialog = InputDialog.Builder(requireContext())
            .setTitle("请输入新用户名称")
            .setContent("新密码默认为123")
            .setCancelText(getString(R.string.cancel))
            .setConfirmText(getString(R.string.confirm))
            .setMaxLength(9)
            .setOnConfirmClick(object : InputDialog.InputDialogListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onConfirmClicked(inputText: String, password: String) {
                    val user = User(inputText, "123")
                    addUser(user)
                    addDialog.dismiss()
                    userList.add(user)
                    userAdapter.notifyDataSetChanged()

                }

            })
            .build()

    }

    private fun initView() {
        binding.returnLl.setOnClickListener { view ->
            Navigation.findNavController(view).navigateUp()
        }

        binding.tvAdd.setOnClickListener {
            addDialog.show()
        }
    }

    override fun onEditClick(user: User, position: Int) {
        passwordDialog = InputDialog.Builder(requireContext())
            .setTitle("请输入新密码")
            .setCancelText(getString(R.string.cancel))
            .setConfirmText(getString(R.string.confirm))
            .setMaxLength(9)
            .setOnConfirmClick(object : InputDialog.InputDialogListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onConfirmClicked(inputText: String, password: String) {
                    changePassword(user.username, inputText)
                    passwordDialog.dismiss()
                    userList[position] = User(user.username, inputText)
                    userAdapter.notifyDataSetChanged()
                }

            })
            .build()
        passwordDialog.show()

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteClick(user: User, position: Int) {
        userList.removeAt(position)
        userAdapter.notifyDataSetChanged()
        UserRepository.deleteUser(user.username)
    }
}