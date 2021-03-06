package com.example.houshuai.qingdan.alipay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.example.houshuai.qingdan.R;
import com.example.houshuai.qingdan.shopcar.QingdanShopingCarData;
import com.squareup.picasso.Picasso;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AlipayActivity extends FragmentActivity {

    // 商户PID
    public static final String PARTNER = "2088111278561763";
    // 商户收款账号
    public static final String SELLER = "gaoyandingzhi@126.com";
    // 商户私钥，pkcs8格式
    //上传给远程服务器的信息，购物的信息，RSA_PRIVATE
    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANEClM9ja39OuhbiFcPYG8nUt19TIGvnBjC2CGMV3BKY2pTolVuicMfM0yyxvwtewe7Wkk+06Zl8fjgIWZS8SsfOeznQZbJq236CbcFYIhDsorDllDwQ0Uk409WSjaOCDJamOjGeQjYqy3D7v+z+Z48ZvCOPleX2h415mHQeHWVdAgMBAAECgYB6FrHqOr7uTIRzHXltPu1shi7fJeWIYhjBl3NqvbghvNvho8KrFkYez8yDDQj1kVJjOz+YA6t4lrn77RS2xw4+fRJgBy/LD9ILectaThysuFt84yKooSuFAv1AQKMeVXkpnFuzzBFtxyuRPtPUYXftSvEm/9BapFHGEVCuT7RvAQJBAP9yq18VFhPQAfngld9n0NwmCO33kdbFYqVIWBNKZdvVZIqwIvnmTqsgQacrvWutsWauukKT7VzySkht/uE63j0CQQDRdjgqx4H7SfMjkaZK5nJ6ptuFgR19HkakOJZSIM78Ot3PzfHcnfYuCRjs8lIEWmhYqj2FE+BcZ9cejphGuTWhAkB0XimBXBq9ldGAonXD2whDcbQ5q8EtJKgmgUlWKFs0hQaTQ1/7lZYa0Mv3uq5EwlCBZXGGaNsFr351dl5Y/jdFAkA6D2DmSsL22rqwo1DK9jHJWbMDwJRh+CBwqNbSERIOzGprjZR7KLXycMcd9tVRK5Y87YN7/dR1CLuSVshS4kfBAkAW6ls9/RlBA6gOpDuq+Qn4CZUng3h7OJsDgzCY95RtuMISJNuVFcGC/XVKB+urkyfhR/H7I8HIPXQtNJenH9f2";


    // 支付宝公�??
    public static final String RSA_PUBLIC = "";//公钥保存在服务器�??

    private static final int SDK_PAY_FLAG = 1;
    private ImageView iv;
    private TextView bt_back,tvPrice,tvNum,tvTotal,tvTotal2,tvName,tvCity;
    private String name,city,price,num,imgUrl;
    private double totalPrice;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    AliPayResult payResult = new AliPayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回�??要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为�??9000”则代表支付成功，具体状态码代表含义可参考接口文�??
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(AlipayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失�??
                        // "8000"代表支付结果因为支付渠道原因或�?�系统原因还在等待支付结果确认，�??终交易是否成功以服务端异步�?�知为准（小概率状�?�）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(AlipayActivity.this, "支付结果确认�??", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(AlipayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buynow);
        iv= (ImageView) findViewById(R.id.qingdan_pay_img);
        tvNum= (TextView) findViewById(R.id.qingdan_pay_num);
        tvName= (TextView) findViewById(R.id.qingdan_pay_name);
        tvPrice= (TextView) findViewById(R.id.qingdan_pay_price);
        tvTotal= (TextView) findViewById(R.id.qingdan_pay_total);
        tvTotal2= (TextView) findViewById(R.id.qingdan_pay_total2);
        tvCity= (TextView) findViewById(R.id.qingdan_pay_city);
        Intent intent=getIntent();
        name=intent.getStringExtra("goodName");
        Log.i("TAG","------------onCreat()::"+name);
        city=intent.getStringExtra("shipCity");
        price=(intent.getIntExtra("price",0)/100)+"";
        imgUrl=intent.getStringExtra("imgUrl");
        num= (String)QingdanShopingCarData.arrayList_cart.get(QingdanShopingCarData.arrayList_cart.size()-1).get("num");
        initView();
    }

    private void initView() {
        bt_back=(TextView) findViewById(R.id.bt_buy_back);
        bt_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        if (!"".equals(imgUrl))
        {
            Picasso.with(this).load(imgUrl).placeholder(R.drawable.loading_placeholder).into(iv);
        }
        Log.i("TAG","------PAY:::"+name);
        tvName.setText(name);
        tvCity.setText("由"+city+"发货");
        tvPrice.setText(price);
        tvNum.setText("X"+num);
        totalPrice=Integer.parseInt(num)*Double.parseDouble(price);
        tvTotal.setText("￥"+totalPrice);
        tvTotal2.setText("￥"+totalPrice);

    }

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */

    public void pay(View v) {

        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("�??要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }
        String orderInfo = getOrderInfo(name, "发货地"+city, totalPrice+"");

        /**
         * 特别注意，这里的签名逻辑�??要放在服务端，切勿将私钥泄露在代码中�??
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信�??
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构�?�PayTask 对象
                PayTask alipay = new PayTask(AlipayActivity.this);
                // 调用支付接口，获取支付结�??
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }





    /**
     * get the sdk version. 获取SDK版本�??
     *
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * 原生的H5（手机网页版支付切natvie支付�?? 【对应页面网页支付按钮�??
     *
     * @param v
     */
    public void h5Pay(View v) {
        Intent intent = new Intent(this, AlipayH5PayActivity.class);
        Bundle extras = new Bundle();
        /**
         * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity�??
         * demo中拦截url进行支付的�?�辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现�??
         * 商户可以根据自己的需求来实现
         */
        String url = "http://m.taobao.com";
        // url可以是一号店或�?�淘宝等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
        extras.putString("url", url);
        intent.putExtras(extras);
        startActivity(intent);
    }

    /**
     * create the order info. 创建订单信息
     *
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账�??
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单�??
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步�?�知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称�?? 固定�??
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型�?? 固定�??
        orderInfo += "&payment_type=\"1\"";

        // 参数编码�?? 固定�??
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭�??
        // 取�?�范围：1m�??15d�??
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）�??
        // 该参数数值不接受小数点，�??1.5h，可转换�??90m�??
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支�??
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可�??
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，�??配置此参数，参与签名�?? 固定�?? （需要签约�?�无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该�?�在商户端应保持唯一（可自定义格式规范）
     *
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签�??
     *
     * @param content
     *            待签名订单信�??
     */
    private String sign(String content) {
        return AlipaySignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
