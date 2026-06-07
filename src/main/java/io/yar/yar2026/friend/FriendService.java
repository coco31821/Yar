package io.yar.yar2026.friend;

import io.yar.yar2026.friend.domain.FriendRequest;
import io.yar.yar2026.friend.domain.FriendRequestStatus;
import io.yar.yar2026.friend.dto.FriendRequestCreateRequest;
import io.yar.yar2026.friend.dto.FriendRequestResponse;
import io.yar.yar2026.friend.exception.DuplicateFriendRequestException;
import io.yar.yar2026.friend.exception.FriendRequestNotFoundException;
import io.yar.yar2026.friend.exception.InvalidFriendRequestException;
import io.yar.yar2026.friend.exception.SelfFriendRequestException;
import io.yar.yar2026.friend.repository.FriendRequestRepository;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.exception.UserNotFoundException;
import io.yar.yar2026.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public FriendRequestResponse createFriendRequest(
            Long userId,
            FriendRequestCreateRequest request
    ) {
        Long toUserId = request.toUserId();

        if (userId.equals(toUserId)) {
            throw new SelfFriendRequestException();
        }

        User fromUser = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        User toUser = userRepository.findById(toUserId)
                .orElseThrow(UserNotFoundException::new);

        if (friendRequestRepository.existsBetweenUsers(userId, toUserId)) {
            throw new DuplicateFriendRequestException();
        }

        FriendRequest friendRequest = FriendRequest.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();

        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);

        return FriendRequestResponse.from(savedFriendRequest);
    }

    public List<FriendRequestResponse> getReceivedFriendRequests(Long userId) {
        return friendRequestRepository
                .findAllByToUser_UserIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        FriendRequestStatus.PENDING
                )
                .stream()
                .map(FriendRequestResponse::fromReceived)
                .toList();
    }

    public List<FriendRequestResponse> getSentFriendRequests(Long userId) {
        return friendRequestRepository
                .findAllByFromUser_UserIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        FriendRequestStatus.PENDING
                )
                .stream()
                .map(FriendRequestResponse::fromSent)
                .toList();
    }

    @Transactional
    public FriendRequestResponse acceptFriendRequest(Long userId, Long requestId) {
        FriendRequest friendRequest = getFriendRequest(requestId);

        validatePending(friendRequest);
        validateReceiver(userId, friendRequest, "본인에게 온 요청만 수락할 수 있습니다.");

        friendRequest.accept();

        return FriendRequestResponse.fromReceived(friendRequest);
    }

    @Transactional
    public void declineFriendRequest(Long userId, Long requestId) {
        FriendRequest friendRequest = getFriendRequest(requestId);

        validatePending(friendRequest);
        validateReceiver(userId, friendRequest, "본인에게 온 요청만 거절할 수 있습니다.");

        friendRequest.decline();
    }

    @Transactional
    public void cancelFriendRequest(Long userId, Long requestId) {
        FriendRequest friendRequest = getFriendRequest(requestId);

        validatePending(friendRequest);
        validateSender(userId, friendRequest);

        friendRequest.cancel();
    }

    private FriendRequest getFriendRequest(Long requestId) {
        return friendRequestRepository.findById(requestId)
                .orElseThrow(FriendRequestNotFoundException::new);
    }

    private void validatePending(FriendRequest friendRequest) {
        if (friendRequest.getStatus() != FriendRequestStatus.PENDING) {
            throw new InvalidFriendRequestException("대기 중인 친구 요청만 처리할 수 있습니다.");
        }
    }

    private void validateReceiver(
            Long userId,
            FriendRequest friendRequest,
            String message
    ) {
        if (!friendRequest.getToUser().getUserId().equals(userId)) {
            throw new InvalidFriendRequestException(message);
        }
    }

    private void validateSender(Long userId, FriendRequest friendRequest) {
        if (!friendRequest.getFromUser().getUserId().equals(userId)) {
            throw new InvalidFriendRequestException("본인이 보낸 요청만 취소할 수 있습니다.");
        }
    }
}
