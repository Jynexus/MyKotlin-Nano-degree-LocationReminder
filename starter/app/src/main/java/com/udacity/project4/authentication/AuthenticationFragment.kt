package com.udacity.project4.authentication

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand


class AuthenticationFragment : BaseFragment() {

    override val _viewModel: AuthenticationViewModel = AuthenticationViewModel(Application())
    var SIGN_IN_REQUEST_CODE = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(FirebaseAuth.getInstance().currentUser?.displayName != null){
            Toast.makeText(activity,
                "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!",
                Toast.LENGTH_SHORT).show()
            _viewModel.navigationCommand.value =
                NavigationCommand.To(
                    AuthenticationFragmentDirections.actionAuthenticationFragmentToReminderListFragment()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authentication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.button_Login).setOnClickListener { launchSignInFlow() }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Toast.makeText(activity,"Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!",
                    Toast.LENGTH_SHORT).show()
                _viewModel.navigationCommand.postValue(
                    NavigationCommand.To(
                        AuthenticationFragmentDirections.actionAuthenticationFragmentToReminderListFragment()
                    )
                )
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Toast.makeText(activity, "Sign in unsuccessful ${response?.error?.errorCode}",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun launchSignInFlow(){
        // Give users the option to sign in / register with their email or Google account.
        // If users choose to register with their email,
        // they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()

            // This is where you can provide more ways for users to register and
            // sign in.
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_REQUEST_CODE
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            this.SIGN_IN_REQUEST_CODE
        )
    }
}