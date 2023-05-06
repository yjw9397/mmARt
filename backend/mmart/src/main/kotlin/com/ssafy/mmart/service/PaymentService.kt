package com.ssafy.mmart.service

import com.ssafy.mmart.domain.payment.Payment
import com.ssafy.mmart.domain.payment.dto.PaymentReq
import com.ssafy.mmart.domain.paymentDetail.PaymentDetail
import com.ssafy.mmart.exception.bad_request.BadAccessException
import com.ssafy.mmart.exception.not_found.ItemNotFoundException
import com.ssafy.mmart.exception.not_found.PaymentNotFoundException
import com.ssafy.mmart.exception.not_found.UserNotFoundException
import com.ssafy.mmart.repository.ItemRepository
import com.ssafy.mmart.repository.PaymentDetailRepository
import com.ssafy.mmart.repository.PaymentRepository
import com.ssafy.mmart.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService @Autowired constructor(
    val paymentRepository: PaymentRepository,
    val userRepository: UserRepository,
    val itemRepository: ItemRepository,
    val paymentDetailRepository: PaymentDetailRepository,
){
    fun getPayments(userIdx: Int): List<Payment>? {
        userRepository.findByIdOrNull(userIdx) ?: throw UserNotFoundException()
        return paymentRepository.findAllByUser_UserIdx(userIdx)
    }

    fun createPayment(userIdx: Int, paymentReq: PaymentReq): Payment? {
        val user = userRepository.findByIdOrNull(userIdx) ?: throw UserNotFoundException()
        val payment = paymentRepository.save(paymentReq.toEntity(user))
        for (gotCartItem in paymentReq.gotCartRes.itemList) {
            val item = itemRepository.findByIdOrNull(gotCartItem.itemIdx) ?: throw ItemNotFoundException()
            val paymentDetail = PaymentDetail(
                quantity = gotCartItem.quantity,
                discount = if (gotCartItem.isCoupon) gotCartItem.price - gotCartItem.couponPrice else 0,
                totalPrice = gotCartItem.couponPrice * gotCartItem.quantity,
                payment = payment,
                item = item,
            )
            paymentDetailRepository.save(paymentDetail)
        }
        return payment
    }

    @Transactional
    fun deletePayment(paymentIdx: Int, userIdx: Int): Payment? {
        val user = userRepository.findByIdOrNull(userIdx) ?: throw UserNotFoundException()
        val payment = paymentRepository.findByIdOrNull(paymentIdx) ?: throw PaymentNotFoundException()
        if (payment.user == user) {
            paymentRepository.deleteById(payment.paymentIdx!!)
            return payment
        } else {
            throw BadAccessException()
        }
    }
}