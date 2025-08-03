package com.cypress.cymediaplayer.data.repositories

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.cypress.cymediaplayer.data.local.dto.QrCodeData
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.nio.ByteBuffer

class QrCodeAnalyzerRepository(
    private val onQrCodeScanned : (QrCodeData)-> Unit
): ImageAnalysis.Analyzer {

    private val supportImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888,
    )

    @SuppressLint("CheckResult")
    override fun analyze(image: ImageProxy) {
        if(image.format in supportImageFormats){
            val bytes = image.planes.first().buffer.toByteArray()
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            val binaryBmp = BinaryBitmap(HybridBinarizer(source))
            try{
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE
                            )
                        )
                    )
                }.decode(binaryBmp)

                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val tempDto = moshi.adapter(QrCodeData::class.java).fromJson(result.text)
                if(tempDto != null) onQrCodeScanned(tempDto)

            }catch (e: Exception){
                e.printStackTrace()
            }finally {
                image.close()
            }
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray{
        rewind()
        return ByteArray(remaining()).also{
            get(it)
        }
    }

}