package wannabit.io.ringowallet.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import wannabit.io.ringowallet.R;
import wannabit.io.ringowallet.acticites.CreateWalletActivity;
import wannabit.io.ringowallet.base.BaseConstant;
import wannabit.io.ringowallet.base.BaseFragment;
import wannabit.io.ringowallet.task.InitMnemonicTask;
import wannabit.io.ringowallet.task.TaskResult;
import wannabit.io.ringowallet.task.TaskCallback;
import wannabit.io.ringowallet.utils.WKeyUtils;
import wannabit.io.ringowallet.utils.WLog;
import wannabit.io.ringowallet.utils.WUtils;

public class CheckMnemonicFragment extends BaseFragment implements View.OnClickListener, TaskCallback {

    private TextView            mTvMsg01, mTvMsg02;
    private LinearLayout        mLayerWords, mLayerExamples;
    private TextView[]          mTvChecks = new TextView[12];
    private TextView[]          mTvExamples = new TextView[6];
    private Button              mBtnSkip;

    private ArrayList<String>   mWords = new ArrayList<>();
    private ArrayList<String>   mExamples = new ArrayList<>();
    private ArrayList<Integer>  mQuestion = new ArrayList<>();
    private String              mSeed;

    private int mPassStep = 0;
    private int mWrongCnt = 0;

    public static CheckMnemonicFragment newInstance() {
        CheckMnemonicFragment fragment = new CheckMnemonicFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView   = inflater.inflate(R.layout.fragment_check_mnemonic, container, false);
        mLayerWords     = rootView.findViewById(R.id.layer_words);
        mLayerExamples  = rootView.findViewById(R.id.layer_examples);
        mTvMsg01        = rootView.findViewById(R.id.tv_confirm_msg0);
        mTvMsg02        = rootView.findViewById(R.id.tv_confirm_msg1);
        mBtnSkip        = rootView.findViewById(R.id.btn_Skip);
        mTvMsg01.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        mTvMsg02.setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        if(!BaseConstant.IS_SHOWLOG) mBtnSkip.setVisibility(View.GONE);


        for(int i = 0; i < mTvChecks.length; i++) {
            mTvChecks[i] = rootView.findViewById(getResources().getIdentifier("tv_check_" + i , "id", getBaseActivity().getPackageName()));
            mTvChecks[i].setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
        }

        for(int i = 0; i < mTvExamples.length; i++) {
            mTvExamples[i] = rootView.findViewById(getResources().getIdentifier("tv_example_" + i , "id", getBaseActivity().getPackageName()));
            mTvExamples[i].setTypeface(WUtils.getTypefaceRegular(getBaseActivity()));
            mTvExamples[i].setOnClickListener(this);
        }
        mWords = getArguments().getStringArrayList("words");
        mSeed  = getArguments().getString("seed");
        mBtnSkip.setOnClickListener(this);

        onInitViews();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((CreateWalletActivity)getBaseActivity()).onUpdateTitle(getString(R.string.title_confirm_new_mnemonic));
    }


    private void onInitViews() {
        mWrongCnt = 0;
        mQuestion.clear();
        do {
            int random = new Random().nextInt(12);
            if(!mQuestion.contains(random) && !mQuestion.contains(random + 1) && !mQuestion.contains(random - 1)) {
                mQuestion.add(random);
            }

        } while (mQuestion.size() < 3);
        Collections.sort(mQuestion);

        for(int i = 0; i < mTvChecks.length; i++) {
            if(mQuestion.contains(i)) {
                mTvChecks[i].setText("?????");
            } else {
                mTvChecks[i].setText(mWords.get(i));
            }
        }
        onRefreshExample();
        onStartBlink();
    }

    private void updateNextStep() {
        if(mPassStep == 2) {
            mLayerExamples.setVisibility(View.INVISIBLE);
            getBaseActivity().onShowWaitDialog();
            new InitMnemonicTask(getBaseApplication(), this).execute(mSeed);

        } else {
            mPassStep ++;
            onRefreshExample();
            onStartBlink();
        }
    }


    private void onRefreshExample() {
        mExamples.clear();
        mExamples.add(mWords.get(mQuestion.get(mPassStep)));
        do {
            String randomExample = WKeyUtils.getRandomWord();
            if(!mWords.contains(randomExample))
                mExamples.add(randomExample);

        } while (mExamples.size() < 6);
        Collections.shuffle(mExamples, new Random(System.nanoTime()));
        for(int i = 0; i < mTvExamples.length; i++) {
            mTvExamples[i].clearAnimation();
            mTvExamples[i].setText(mExamples.get(i));
            mTvExamples[i].setClickable(true);
            mTvExamples[i].setVisibility(View.VISIBLE);
        }

        AnimationSet fadeAnim = WUtils.getFadeAnim();
        fadeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLayerExamples.setVisibility(View.VISIBLE);
            }


        });
        mLayerExamples.setVisibility(View.INVISIBLE);
        mLayerExamples.startAnimation(fadeAnim);
    }



    private void onStopBlink() {
        for(int i = 0; i < mTvChecks.length; i++) {
            mTvChecks[i].clearAnimation();
        }
    }

    private void onStartBlink() {
        AlphaAnimation ani= new AlphaAnimation(1, 0);
        ani.setDuration(1200);
        ani.setInterpolator(new LinearInterpolator());
        ani.setRepeatCount(Animation.REVERSE);
        ani.setRepeatCount(Animation.INFINITE);
        mTvChecks[mQuestion.get(mPassStep)].setTextColor(getResources().getColor(R.color.color_font_red));
        mTvChecks[mQuestion.get(mPassStep)].setBackground(getResources().getDrawable(R.drawable.roundbox_word_unchecked));
        mTvChecks[mQuestion.get(mPassStep)].startAnimation(ani);
    }

    private void onShakeView() {
        mLayerWords.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(getBaseActivity(), R.anim.shake);
        animation.reset();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                onStartBlink();
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        mLayerWords.startAnimation(animation);
    }

    private void onMatchingAnim(final View from, final View to, final String word) {
        AnimatorSet matchAnim = WUtils.getMatchingAnim(from, to);
        matchAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                int[] fromLoc = new int[2];
                int[] toLoc = new int[2];
                from.getLocationOnScreen(fromLoc);
                to.getLocationOnScreen(toLoc);

                ((TextView)to).setTextColor(getResources().getColor(R.color.color_white));
                ((TextView)to).setText(word);
                to.setBackground(getResources().getDrawable(R.drawable.roundbox_word_checked));
                from.setTranslationX(fromLoc[0] - toLoc[0]);
                from.setTranslationY(fromLoc[1] - toLoc[1]);
                to.clearAnimation();

                updateNextStep();
            }
        });
        matchAnim.start();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mBtnSkip)) {
            getBaseActivity().onShowWaitDialog();
            new InitMnemonicTask(getBaseApplication(), this).execute(mSeed);


        } else if(view instanceof TextView) {
            onStopBlink();
            String clicked = ((TextView)view).getText().toString();
            if(clicked.equals(mWords.get(mQuestion.get(mPassStep)))) {
                for(int i = 0; i < mTvExamples.length; i++) {
                    mTvExamples[i].setClickable(false);
                }
                onMatchingAnim(view, mTvChecks[mQuestion.get(mPassStep)], clicked);
                mWrongCnt = 0;

            } else {
                onShakeView();
                view.setVisibility(View.INVISIBLE);
                mWrongCnt ++;
                if(mWrongCnt > 2) {
                    Toast.makeText(getBaseActivity(), getString(R.string.msg_confirm_failed), Toast.LENGTH_SHORT).show();
                    getBaseActivity().onBackPressed();
                }
            }
        }
    }

    @Override
    public void onTaskResponse(TaskResult result) {
        if(!isAdded()) return;
        getBaseActivity().onHideWaitDialog();
        if (result.taskType == BaseConstant.TASK_INIT_MN) {
            if(result.isSuccess) {
                getBaseActivity().onStartMainActivity();
            } else {}
        }
    }
}