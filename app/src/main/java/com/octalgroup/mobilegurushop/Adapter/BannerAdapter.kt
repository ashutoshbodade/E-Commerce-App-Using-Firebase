package com.octalgroup.android.Adapter

import com.octalgroup.mobilegurushop.Model.Banner
import com.octalgroup.mobilegurushop.ProductView
import com.octalgroup.mobilegurushop.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide


class BannerAdapter(private var context: Context, var listbanner:ArrayList<Banner>) : PagerAdapter(){

    private var layoutInflater: LayoutInflater?= null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return  view == `object`
    }

    override fun getCount(): Int {
        return  listbanner.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val img =listbanner[position].bannerurl


        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = layoutInflater!!.inflate(R.layout.custom_layout, null)
        val image = v.findViewById<View>(R.id.banner_view) as ImageView

        v.setOnClickListener {
            val intent= Intent(context, ProductView::class.java)
            intent.putExtra("id",listbanner[position].productid.toString())
            context.startActivity(intent)
        }

        Glide.with(context).load(img).into(image)
        //image.setImageResource(images[position])
        val vp= container as ViewPager
        vp.addView(v, 0)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val v = `object` as View
        vp.removeView(v)

    }
}