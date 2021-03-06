package com.example.houshuai.qingdan.UI.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.example.houshuai.qingdan.Base.BaseFragment;
import com.example.houshuai.qingdan.R;
import com.example.houshuai.qingdan.UI.Activity.QingdanSecondActivity;
import com.example.houshuai.qingdan.UI.Activity.select_buy_Activity;
import com.example.houshuai.qingdan.adapter.Self_ShangPinRecycleViewAdapter;
import com.example.houshuai.qingdan.dao.ShangPin;
import com.example.houshuai.qingdan.utils.ShangPinDBHelper;
import com.example.houshuai.qingdan.utils.WenZhangDBHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by HouShuai on 2016/7/11.
 */

public class Self_ShangPinFragment extends BaseFragment {

    @BindView(R.id.self_recycle_view)
    RecyclerView mRecyclerView;
    private List<ShangPin> mList = new ArrayList<>();
    @BindView(R.id.swip_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Self_ShangPinRecycleViewAdapter self_recycleViewAdapter;

    public void initRefresh() {
        mSwipeRefreshLayout.setProgressViewOffset(true, -20, 100);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swiperefresh_color1, R.color.swiperefresh_color2, R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        //刷新接口
//                        mList.clear();
                        mList = ShangPinDBHelper.getInstance(getActivity()).getMessageInfoList();
                    }
                }).start();
                self_recycleViewAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected int getFragmentID() {
        return R.layout.self_recycleview;
    }

    @Override
    protected void initFragment() {
        initRecycleView();
        initRefresh();

    }

    private void initRecycleView() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //删除所有数据
        WenZhangDBHelper.getInstance(getActivity()).clearMessageInfo();

        //初始化数据
        initData();
        self_recycleViewAdapter = new Self_ShangPinRecycleViewAdapter(getActivity(), mList);
        mRecyclerView.setAdapter(self_recycleViewAdapter);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(16);
        mRecyclerView.addItemDecoration(spacesItemDecoration);
        initRecycleViewListener();
    }

    private void initRecycleViewListener() {
        self_recycleViewAdapter.setOnClickListener(new Self_ShangPinRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void ItemClickListener(View view, int postion) {
                String shangPin = mList.get(postion).getUrl();
                if ("http" == shangPin.substring(0, 3)) {
                    Intent intent = new Intent(getActivity(), select_buy_Activity.class);
                    intent.putExtra("buylink", shangPin);
                    startActivity(intent);
                    // TODO: 2016/7/12
                } else {
                    Intent intent = new Intent(getActivity(), QingdanSecondActivity.class);
                    intent.putExtra("event_id", shangPin);
                    startActivity(intent);
                }


            }

            @Override
            public void ItemLongClickListener(View view, final int postion) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("是否删除")
                        .setMessage("主人!我不想离开您")
                        .setPositiveButton("残忍抛弃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ShangPinDBHelper.getInstance(getActivity()).deleteMessageInfoList(mList.get(postion).getId());
                                        mList.remove(postion);
                                    }
                                }).start();
                                self_recycleViewAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
            }
        });


    }


    private void initData() {
        //删除所有数据
//        ShangPinDBHelper.getInstance(getActivity()).clearMessageInfo();


        ShangPin wenZhang = new ShangPin();
        wenZhang.setAddLove("10000");
        wenZhang.setImage("http://img3.imgtn.bdimg.com/it/u=2527393055,352242730&fm=21&gp=0.jpg");
        wenZhang.setTitle("你好");
        wenZhang.setAddSee("4000");
        wenZhang.setUrl("http://img3.imgtn.bdimg.com/it/u=2527393055,352242730&fm=21&gp=0.jpg");
        ShangPin wenZhan2g = new ShangPin();
        wenZhan2g.setAddLove("15440");
        wenZhan2g.setImage("http://img4.imgtn.bdimg.com/it/u=256000815,1063265887&fm=21&gp=0.jpg");
        wenZhan2g.setTitle("你好");
        wenZhan2g.setAddSee("100");
        wenZhan2g.setUrl("http://img4.imgtn.bdimg.com/it/u=256000815,1063265887&fm=21&gp=0.jpg");
        ShangPinDBHelper.getInstance(getActivity())
                .addToMessageInfoTable(wenZhang);
        ShangPinDBHelper.getInstance(getActivity())
                .addToMessageInfoTable(wenZhan2g);


        mList = ShangPinDBHelper.getInstance(getActivity()).getMessageInfoList();

    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        private SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }

        }
    }


}
