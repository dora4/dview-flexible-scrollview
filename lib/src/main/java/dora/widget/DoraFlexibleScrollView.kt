package dora.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ScrollView

class DoraFlexibleScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                       defStyleAttr: Int = 0,
                                                       defStyleRes: Int = 0)
    : ScrollView(context, attrs, defStyleAttr, defStyleRes) {

    private var inner: View? = null
    private var y = 0f
    private val normal = Rect()
    private val size = 3

    /**
     * 获得第一个view
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            inner = getChildAt(0)
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (inner == null) {
            return super.onTouchEvent(ev)
        } else {
            commOnTouchEvent(ev)
        }
        return super.onTouchEvent(ev)
    }

    /**
     * 添加手势响应事件
     * @param ev
     */
    fun commOnTouchEvent(ev: MotionEvent) {
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> y = ev.y
            MotionEvent.ACTION_UP -> if (isNeedAnimation()) {
                animation()
            }

            MotionEvent.ACTION_MOVE -> {
                val preY = y
                val nowY = ev.y
                val deltaY = (preY - nowY).toInt() / size
                y = nowY
                // 当滚动到最上或者最下时就不会再滚动，这时移动布局
                if (isNeedMove()) {
                    if (normal.isEmpty) {
                        // 保存正常的布局位置
                        normal[inner!!.left, inner!!.top, inner!!.right] = inner!!.bottom
                        return
                    }
                    //这里移动布局
                    inner!!.layout(
                        inner!!.left, inner!!.top - deltaY, inner!!.right,
                        inner!!.bottom - deltaY
                    )
                }
            }
        }
    }

    // 开启动画移动
    fun animation() {
        // 开启移动动画
        val ta = TranslateAnimation(0f, 0f, (inner!!.top - normal.top).toFloat(), 0f)
        ta.duration = 200
        inner!!.startAnimation(ta)
        // 设置回到正常的布局位置
        inner!!.layout(normal.left, normal.top, normal.right, normal.bottom)
        normal.setEmpty()
    }

    // 是否需要开启动画
    fun isNeedAnimation(): Boolean {
        return !normal.isEmpty
    }

    // 是否需要移动布局
    fun isNeedMove(): Boolean {
        val offset = inner!!.measuredHeight - height
        val scrollY = scrollY
        return if (scrollY == 0 || scrollY == offset) {
            true
        } else false
    }
}