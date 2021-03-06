package com.example.houshuai.qingdan.UI.Activity;


import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.houshuai.qingdan.Base.BaseActivity;
import com.example.houshuai.qingdan.R;
import com.example.houshuai.qingdan.adapter.QingdanSecond_GridView_Adapter;
import com.example.houshuai.qingdan.bean.Constant;
import com.example.houshuai.qingdan.bean.ZhuanchangBean;
import com.example.houshuai.qingdan.dao.WenZhang;
import com.example.houshuai.qingdan.inter.QingdanInter;
import com.example.houshuai.qingdan.shopcar.QingdanThridActivity;
import com.example.houshuai.qingdan.utils.RetrofitUtils;
import com.example.houshuai.qingdan.utils.WenZhangDBHelper;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 清单中第二个页面(专场详情)
 * Created by Dufei on 2016/7/9.
 */

public class QingdanSecondActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.backdrop)
    ImageView iv;
    @BindView(R.id.qingdan_floatingbutton)
    FloatingActionButton fab;
    GridView gridView;
    private QingdanInter serverInter;
    private Call<ZhuanchangBean> call_result;
    private List<ZhuanchangBean.MartshowItemsBean> list = new LinkedList<>();
    private String event_id;
    private QingdanSecond_GridView_Adapter adapter;
    private ZhuanchangBean zhuanchangBean;

    @Override
    protected void initLayout() {
        Intent intent = getIntent();
        event_id = intent.getStringExtra("event_id");
        serverInter = RetrofitUtils.initRetrofit(Constant.BASE_URL_ZHUANCHANG);
        list = getZhuanchangList(event_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        aboutgridView(list);
        aboutFloatingButton();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case android.R.id.home:
                        QingdanSecondActivity.this.finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }


    /**
     * 收藏按钮
     */
    private void aboutFloatingButton() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                WenZhang wenZhang = new WenZhang();
                wenZhang.setAddLove(zhuanchangBean.getCount() + "");
                wenZhang.setAddSee(zhuanchangBean.getCount() + "");
                wenZhang.setContent(zhuanchangBean.getBrand_story());
                wenZhang.setImage(zhuanchangBean.getMain_img());
                wenZhang.setTitle(zhuanchangBean.getTitle());
                wenZhang.setUrl(zhuanchangBean.getEvent_id());
                WenZhangDBHelper.getInstance(QingdanSecondActivity.this).addToMessageInfoTable(wenZhang);
                //String url=Constant.BASE_URL_ZHUANCHANG+event_id+"-1-40-hot-1--0-0-.html?package=mizhe&&user_label=1";
                Toast.makeText(QingdanSecondActivity.this, "收藏成功！", Toast.LENGTH_LONG).show();
                //TODO
                String url = Constant.BASE_URL_ZHUANCHANG + event_id + "-1-40-hot-1--0-0-.html?package=mizhe&&user_label=1";

            }
        });
    }

    /**
     * 获取数据源
     */
    private List<ZhuanchangBean.MartshowItemsBean> getZhuanchangList(final String event_id) {
        call_result = serverInter.getZhuanchangInfo(event_id);
        call_result.enqueue(new Callback<ZhuanchangBean>() {
            @Override
            public void onResponse(Call<ZhuanchangBean> call, Response<ZhuanchangBean> response) {
                if (response.isSuccess() && response.body() != null) {
                    zhuanchangBean = response.body();
                    list = response.body().getMartshow_items();
                    collapsingToolbar.setTitle(response.body().getSeller_title());
                    Picasso.with(QingdanSecondActivity.this).load(response.body().getMain_img()).placeholder(R.drawable.loading_placeholder).into(iv);
                    Log.i("Tag", "index2------>" + event_id);
                    Log.i("TAG", "LIST---->" + list.size() + "-------" + list.get(0).getTitle());
                    adapter.reloadData(list, true);
                    Log.i("Tag", "index2------>" + event_id);
                    Log.i("TAG", "LIST---->" + list.size() + "-------" + list.get(0).getTitle());
                    adapter.reloadData(list, true);
                }
            }

            @Override
            public void onFailure(Call<ZhuanchangBean> call, Throwable t) {
                Toast.makeText(QingdanSecondActivity.this, "网络异常，加载失败！", Toast.LENGTH_SHORT).show();
            }
        });

        return list;
    }

    @Override
    protected int getActivityID() {
        return R.layout.activity_qingdan_second;
    }

    private void aboutgridView(final List<ZhuanchangBean.MartshowItemsBean> list) {
        gridView = (GridView) findViewById(R.id.gridview_id);
        adapter = new QingdanSecond_GridView_Adapter(list, QingdanSecondActivity.this);
        gridView.setAdapter(adapter);
        gridView.setEmptyView(getLayoutInflater().inflate(R.layout.qingdan_list_empty, null));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Tag", "--------gridView--click" + i);
                int iid = list.get(i).getIid();
                String name = list.get(i).getTitle();

                Intent intent = new Intent(QingdanSecondActivity.this, QingdanThridActivity.class);
                intent.putExtra("iid", iid);
                gridView = (GridView) findViewById(R.id.gridview_id);
                adapter = new QingdanSecond_GridView_Adapter(list, QingdanSecondActivity.this);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.i("Tag", "--------gridView--click" + i);
                        int iid = list.get(i).getIid();
                        Intent intent = new Intent(QingdanSecondActivity.this, QingdanThridActivity.class);
                        intent.putExtra("iid", iid);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
