package fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import tv.cloudwalker.cwnxt.cloudwalkercompanion.MainActivity;
import tv.cloudwalker.cwnxt.cloudwalkercompanion.R;
import viewModel.TvRemoteViewModel;

public class TvRemoteFragment extends Fragment
{
    private static final String TAG = "TvRemoteFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return  inflater.inflate(R.layout.fragment_remote_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Vibrator vibe = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        final TvRemoteViewModel tvRemoteViewModel = ViewModelProviders.of(this).get(TvRemoteViewModel.class);

        tvRemoteViewModel.getNsdMessageSentStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.d(TAG, "onChanged: message send status "+aBoolean);
                if(aBoolean != null && !aBoolean) {
                    ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                }
            }
        });

        view.findViewById(R.id.upButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                if(((MainActivity)getActivity()).getNsdHost() != null){
                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "19");
                }
                else {
                    ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    Toast.makeText(view.getContext(), "Not Yet Connected To any Cloudwalker TV", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                if(((MainActivity)getActivity()).getNsdHost() != null){
                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "21");
                }else {
                    ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    Toast.makeText(view.getContext(), "Not Yet Connected To an  y Cloudwalker TV", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                if(((MainActivity)getActivity()).getNsdHost() != null){
                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "22");
                }else {
                    ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    Toast.makeText(view.getContext(), "Not Yet Connected To any Cloudwalker TV", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.downButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                if(((MainActivity)getActivity()).getNsdHost() != null){
                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "20");
                }else {
                    ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    Toast.makeText(view.getContext(), "Not Yet Connected To any Cloudwalker TV", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                if(((MainActivity)getActivity()).getNsdHost() != null){
                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "23");
                }else {
                    ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    Toast.makeText(view.getContext(), "Not Yet Connected To any Cloudwalker TV", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.backbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                if(((MainActivity)getActivity()).getNsdHost() != null){
                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "4");
                }else {
                    ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    Toast.makeText(view.getContext(), "Not Yet Connected To any Cloudwalker TV", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.homebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(20);
                if(((MainActivity)getActivity()).getNsdHost() != null){
                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "3");
                }else {
                    ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    Toast.makeText(view.getContext(), "Not Yet Connected To any Cloudwalker TV", Toast.LENGTH_SHORT).show();
                }
            }
        });





//
//        pieMenu = new RadialMenuWidget(getContext());
//        upItem = new RadialMenuItem("up","");
//        downItem = new RadialMenuItem("down","");
//        rightItem = new RadialMenuItem("right","");
//        leftItem = new RadialMenuItem("left","");
//        okItem = new RadialMenuItem("ok","");
//
//        upItem.setDisplayIcon(R.drawable.up_arrow);
//        downItem.setDisplayIcon(R.drawable.down_arrow);
//        leftItem.setDisplayIcon(R.drawable.left_arrow);
//        rightItem.setDisplayIcon(R.drawable.right_arrow);
//
//
//
//        final TvRemoteViewModel tvRemoteViewModel = ViewModelProviders.of(this).get(TvRemoteViewModel.class);
//
//        tvRemoteViewModel.getNsdMessageSentStatus().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable Boolean aBoolean) {
//                Log.d(TAG, "onChanged: message send status "+aBoolean);
//            }
//        });
//
//        okItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
//            @Override
//            public void execute() {
//                if(((MainActivity)getActivity()).getNsdHost() != null && ((MainActivity)getActivity()).getNsdPort() > 0) {
//                    vibe.vibrate(20);
//                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "23");
//                }
//            }
//        });
//        upItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
//            @Override
//            public void execute() {
//                if(((MainActivity)getActivity()).getNsdHost() != null && ((MainActivity)getActivity()).getNsdPort() > 0) {
//                    vibe.vibrate(20);
//                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "19");
//                }
//            }
//        });
//        downItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
//            @Override
//            public void execute() {
//                if(((MainActivity)getActivity()).getNsdHost() != null && ((MainActivity)getActivity()).getNsdPort() > 0) {
//                    vibe.vibrate(20);
//                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "20");
//                }
//            }
//        });
//        leftItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
//            @Override
//            public void execute() {
//                if(((MainActivity)getActivity()).getNsdHost() != null && ((MainActivity)getActivity()).getNsdPort() > 0) {
//                    vibe.vibrate(20);
//                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "21");
//                }
//            }
//        });
//        rightItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
//            @Override
//            public void execute() {
//                if(((MainActivity)getActivity()).getNsdHost() != null && ((MainActivity)getActivity()).getNsdPort() > 0) {
//                    vibe.vibrate(20);
//                    tvRemoteViewModel.sentNsdMessageFromRepository(((MainActivity)getActivity()).getNsdHost(), ((MainActivity)getActivity()).getNsdPort(), "22");
//                }
//            }
//        });

////        rightItem.menuActiviated();
////        leftItem.menuActiviated();
////        upItem.menuActiviated();
////        downItem.menuActiviated();
////        okItem.menuActiviated();
//
//        pieMenu.addMenuEntry(upItem);
//        pieMenu.addMenuEntry(rightItem);
//        pieMenu.addMenuEntry(downItem);
//        pieMenu.addMenuEntry(leftItem);
//        pieMenu.setCenterCircle(okItem);
////        pieMenu.setInnerRingColor(ContextCompat.getColor(view.getContext(), R.color.youtubeRed),  70);
//        pieMenu.setOuterRingColor(ContextCompat.getColor(view.getContext(), R.color.navBackgroundColor),  70);
//        pieMenu.setSelectedColor(ContextCompat.getColor(view.getContext(), R.color.highlightColor),  70);
//        pieMenu.setOutlineColor(ContextCompat.getColor(view.getContext(), android.R.color.black),  100);
//        pieMenu.setCenterCircleRadius(50);
//
////        pieMenu.setAnimationSpeed(1000L);
//
//        //pieMenu.setSourceLocation(200, 200);
//        //pieMenu.setShowSourceLocation(true);
//        pieMenu.setIconSize(15, 30);
//        pieMenu.setTextSize(13);
//        pieMenu.setInnerRingRadius(56, 140);
//        pieMenu.setOuterRingRadius(100, 140);
//        pieMenu.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        pieMenu.performClick();
//        pieMenu.show(view);
    }
}
