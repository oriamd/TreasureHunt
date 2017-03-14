package com.ori.amd.treasurehunt;

import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

/**
 * Created by Ori on 2/26/2017.
 */

public class AnimatedGameMassages {

    Activity context;
    TextView readyMassage;
    TextView goMassage;

    AlphaAnimation showAlphaAnimation;

    AlphaAnimation hideAlphaAnimation;


    public AnimatedGameMassages(Activity context){
        this.context = context;
        readyMassage = (TextView) context.findViewById(R.id.textView19);
        goMassage = (TextView) context.findViewById(R.id.textView20);

        showAlphaAnimation = new AlphaAnimation(0.0f,1.0f);
        showAlphaAnimation.setDuration(700);
        hideAlphaAnimation = new AlphaAnimation(1.0f,0.0f);
        hideAlphaAnimation.setDuration(700);

    }

    public void showReadyMsg(){
        readyMassage.setVisibility(View.VISIBLE);
        readyMassage.startAnimation(showAlphaAnimation);
    }

    public void hideReadyMsg(){
        readyMassage.setVisibility(View.INVISIBLE);
        readyMassage.startAnimation(hideAlphaAnimation);
    }

    public void showGoMsg(){
        goMassage.setVisibility(View.VISIBLE);
        goMassage.startAnimation(showAlphaAnimation);
    }

    public void hideGoMsg(){
        goMassage.setVisibility(View.INVISIBLE);
        goMassage.startAnimation(hideAlphaAnimation);
    }


}
