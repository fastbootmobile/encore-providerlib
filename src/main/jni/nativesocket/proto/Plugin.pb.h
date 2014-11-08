// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Plugin.proto

#ifndef PROTOBUF_Plugin_2eproto__INCLUDED
#define PROTOBUF_Plugin_2eproto__INCLUDED

#include <string>

#include <google/protobuf/stubs/common.h>

#if GOOGLE_PROTOBUF_VERSION < 2006000
#error This file was generated by a newer version of protoc which is
#error incompatible with your Protocol Buffer headers.  Please update
#error your headers.
#endif
#if 2006001 < GOOGLE_PROTOBUF_MIN_PROTOC_VERSION
#error This file was generated by an older version of protoc which is
#error incompatible with your Protocol Buffer headers.  Please
#error regenerate this file with a newer version of protoc.
#endif

#include <google/protobuf/generated_message_util.h>
#include <google/protobuf/message.h>
#include <google/protobuf/repeated_field.h>
#include <google/protobuf/extension_set.h>
#include <google/protobuf/generated_enum_reflection.h>
#include <google/protobuf/unknown_field_set.h>
// @@protoc_insertion_point(includes)

namespace omnimusic {

// Internal implementation detail -- do not call these.
void  protobuf_AddDesc_Plugin_2eproto();
void protobuf_AssignDesc_Plugin_2eproto();
void protobuf_ShutdownFile_Plugin_2eproto();

class AudioData;
class AudioResponse;
class Request;
class FormatInfo;
class BufferInfo;

enum Request_RequestType {
  Request_RequestType_FORMAT_INFO = 0,
  Request_RequestType_BUFFER_INFO = 1
};
bool Request_RequestType_IsValid(int value);
const Request_RequestType Request_RequestType_RequestType_MIN = Request_RequestType_FORMAT_INFO;
const Request_RequestType Request_RequestType_RequestType_MAX = Request_RequestType_BUFFER_INFO;
const int Request_RequestType_RequestType_ARRAYSIZE = Request_RequestType_RequestType_MAX + 1;

const ::google::protobuf::EnumDescriptor* Request_RequestType_descriptor();
inline const ::std::string& Request_RequestType_Name(Request_RequestType value) {
  return ::google::protobuf::internal::NameOfEnum(
    Request_RequestType_descriptor(), value);
}
inline bool Request_RequestType_Parse(
    const ::std::string& name, Request_RequestType* value) {
  return ::google::protobuf::internal::ParseNamedEnum<Request_RequestType>(
    Request_RequestType_descriptor(), name, value);
}
// ===================================================================

class AudioData : public ::google::protobuf::Message {
 public:
  AudioData();
  virtual ~AudioData();

  AudioData(const AudioData& from);

  inline AudioData& operator=(const AudioData& from) {
    CopyFrom(from);
    return *this;
  }

  inline const ::google::protobuf::UnknownFieldSet& unknown_fields() const {
    return _unknown_fields_;
  }

  inline ::google::protobuf::UnknownFieldSet* mutable_unknown_fields() {
    return &_unknown_fields_;
  }

  static const ::google::protobuf::Descriptor* descriptor();
  static const AudioData& default_instance();

  void Swap(AudioData* other);

  // implements Message ----------------------------------------------

  AudioData* New() const;
  void CopyFrom(const ::google::protobuf::Message& from);
  void MergeFrom(const ::google::protobuf::Message& from);
  void CopyFrom(const AudioData& from);
  void MergeFrom(const AudioData& from);
  void Clear();
  bool IsInitialized() const;

  int ByteSize() const;
  bool MergePartialFromCodedStream(
      ::google::protobuf::io::CodedInputStream* input);
  void SerializeWithCachedSizes(
      ::google::protobuf::io::CodedOutputStream* output) const;
  ::google::protobuf::uint8* SerializeWithCachedSizesToArray(::google::protobuf::uint8* output) const;
  int GetCachedSize() const { return _cached_size_; }
  private:
  void SharedCtor();
  void SharedDtor();
  void SetCachedSize(int size) const;
  public:
  ::google::protobuf::Metadata GetMetadata() const;

  // nested types ----------------------------------------------------

  // accessors -------------------------------------------------------

  // required bytes samples = 1;
  inline bool has_samples() const;
  inline void clear_samples();
  static const int kSamplesFieldNumber = 1;
  inline const ::std::string& samples() const;
  inline void set_samples(const ::std::string& value);
  inline void set_samples(const char* value);
  inline void set_samples(const void* value, size_t size);
  inline ::std::string* mutable_samples();
  inline ::std::string* release_samples();
  inline void set_allocated_samples(::std::string* samples);

  // @@protoc_insertion_point(class_scope:omnimusic.AudioData)
 private:
  inline void set_has_samples();
  inline void clear_has_samples();

  ::google::protobuf::UnknownFieldSet _unknown_fields_;

  ::google::protobuf::uint32 _has_bits_[1];
  mutable int _cached_size_;
  ::std::string* samples_;
  friend void  protobuf_AddDesc_Plugin_2eproto();
  friend void protobuf_AssignDesc_Plugin_2eproto();
  friend void protobuf_ShutdownFile_Plugin_2eproto();

  void InitAsDefaultInstance();
  static AudioData* default_instance_;
};
// -------------------------------------------------------------------

class AudioResponse : public ::google::protobuf::Message {
 public:
  AudioResponse();
  virtual ~AudioResponse();

  AudioResponse(const AudioResponse& from);

  inline AudioResponse& operator=(const AudioResponse& from) {
    CopyFrom(from);
    return *this;
  }

  inline const ::google::protobuf::UnknownFieldSet& unknown_fields() const {
    return _unknown_fields_;
  }

  inline ::google::protobuf::UnknownFieldSet* mutable_unknown_fields() {
    return &_unknown_fields_;
  }

  static const ::google::protobuf::Descriptor* descriptor();
  static const AudioResponse& default_instance();

  void Swap(AudioResponse* other);

  // implements Message ----------------------------------------------

  AudioResponse* New() const;
  void CopyFrom(const ::google::protobuf::Message& from);
  void MergeFrom(const ::google::protobuf::Message& from);
  void CopyFrom(const AudioResponse& from);
  void MergeFrom(const AudioResponse& from);
  void Clear();
  bool IsInitialized() const;

  int ByteSize() const;
  bool MergePartialFromCodedStream(
      ::google::protobuf::io::CodedInputStream* input);
  void SerializeWithCachedSizes(
      ::google::protobuf::io::CodedOutputStream* output) const;
  ::google::protobuf::uint8* SerializeWithCachedSizesToArray(::google::protobuf::uint8* output) const;
  int GetCachedSize() const { return _cached_size_; }
  private:
  void SharedCtor();
  void SharedDtor();
  void SetCachedSize(int size) const;
  public:
  ::google::protobuf::Metadata GetMetadata() const;

  // nested types ----------------------------------------------------

  // accessors -------------------------------------------------------

  // required int32 written = 1;
  inline bool has_written() const;
  inline void clear_written();
  static const int kWrittenFieldNumber = 1;
  inline ::google::protobuf::int32 written() const;
  inline void set_written(::google::protobuf::int32 value);

  // @@protoc_insertion_point(class_scope:omnimusic.AudioResponse)
 private:
  inline void set_has_written();
  inline void clear_has_written();

  ::google::protobuf::UnknownFieldSet _unknown_fields_;

  ::google::protobuf::uint32 _has_bits_[1];
  mutable int _cached_size_;
  ::google::protobuf::int32 written_;
  friend void  protobuf_AddDesc_Plugin_2eproto();
  friend void protobuf_AssignDesc_Plugin_2eproto();
  friend void protobuf_ShutdownFile_Plugin_2eproto();

  void InitAsDefaultInstance();
  static AudioResponse* default_instance_;
};
// -------------------------------------------------------------------

class Request : public ::google::protobuf::Message {
 public:
  Request();
  virtual ~Request();

  Request(const Request& from);

  inline Request& operator=(const Request& from) {
    CopyFrom(from);
    return *this;
  }

  inline const ::google::protobuf::UnknownFieldSet& unknown_fields() const {
    return _unknown_fields_;
  }

  inline ::google::protobuf::UnknownFieldSet* mutable_unknown_fields() {
    return &_unknown_fields_;
  }

  static const ::google::protobuf::Descriptor* descriptor();
  static const Request& default_instance();

  void Swap(Request* other);

  // implements Message ----------------------------------------------

  Request* New() const;
  void CopyFrom(const ::google::protobuf::Message& from);
  void MergeFrom(const ::google::protobuf::Message& from);
  void CopyFrom(const Request& from);
  void MergeFrom(const Request& from);
  void Clear();
  bool IsInitialized() const;

  int ByteSize() const;
  bool MergePartialFromCodedStream(
      ::google::protobuf::io::CodedInputStream* input);
  void SerializeWithCachedSizes(
      ::google::protobuf::io::CodedOutputStream* output) const;
  ::google::protobuf::uint8* SerializeWithCachedSizesToArray(::google::protobuf::uint8* output) const;
  int GetCachedSize() const { return _cached_size_; }
  private:
  void SharedCtor();
  void SharedDtor();
  void SetCachedSize(int size) const;
  public:
  ::google::protobuf::Metadata GetMetadata() const;

  // nested types ----------------------------------------------------

  typedef Request_RequestType RequestType;
  static const RequestType FORMAT_INFO = Request_RequestType_FORMAT_INFO;
  static const RequestType BUFFER_INFO = Request_RequestType_BUFFER_INFO;
  static inline bool RequestType_IsValid(int value) {
    return Request_RequestType_IsValid(value);
  }
  static const RequestType RequestType_MIN =
    Request_RequestType_RequestType_MIN;
  static const RequestType RequestType_MAX =
    Request_RequestType_RequestType_MAX;
  static const int RequestType_ARRAYSIZE =
    Request_RequestType_RequestType_ARRAYSIZE;
  static inline const ::google::protobuf::EnumDescriptor*
  RequestType_descriptor() {
    return Request_RequestType_descriptor();
  }
  static inline const ::std::string& RequestType_Name(RequestType value) {
    return Request_RequestType_Name(value);
  }
  static inline bool RequestType_Parse(const ::std::string& name,
      RequestType* value) {
    return Request_RequestType_Parse(name, value);
  }

  // accessors -------------------------------------------------------

  // required .omnimusic.Request.RequestType request = 1;
  inline bool has_request() const;
  inline void clear_request();
  static const int kRequestFieldNumber = 1;
  inline ::omnimusic::Request_RequestType request() const;
  inline void set_request(::omnimusic::Request_RequestType value);

  // @@protoc_insertion_point(class_scope:omnimusic.Request)
 private:
  inline void set_has_request();
  inline void clear_has_request();

  ::google::protobuf::UnknownFieldSet _unknown_fields_;

  ::google::protobuf::uint32 _has_bits_[1];
  mutable int _cached_size_;
  int request_;
  friend void  protobuf_AddDesc_Plugin_2eproto();
  friend void protobuf_AssignDesc_Plugin_2eproto();
  friend void protobuf_ShutdownFile_Plugin_2eproto();

  void InitAsDefaultInstance();
  static Request* default_instance_;
};
// -------------------------------------------------------------------

class FormatInfo : public ::google::protobuf::Message {
 public:
  FormatInfo();
  virtual ~FormatInfo();

  FormatInfo(const FormatInfo& from);

  inline FormatInfo& operator=(const FormatInfo& from) {
    CopyFrom(from);
    return *this;
  }

  inline const ::google::protobuf::UnknownFieldSet& unknown_fields() const {
    return _unknown_fields_;
  }

  inline ::google::protobuf::UnknownFieldSet* mutable_unknown_fields() {
    return &_unknown_fields_;
  }

  static const ::google::protobuf::Descriptor* descriptor();
  static const FormatInfo& default_instance();

  void Swap(FormatInfo* other);

  // implements Message ----------------------------------------------

  FormatInfo* New() const;
  void CopyFrom(const ::google::protobuf::Message& from);
  void MergeFrom(const ::google::protobuf::Message& from);
  void CopyFrom(const FormatInfo& from);
  void MergeFrom(const FormatInfo& from);
  void Clear();
  bool IsInitialized() const;

  int ByteSize() const;
  bool MergePartialFromCodedStream(
      ::google::protobuf::io::CodedInputStream* input);
  void SerializeWithCachedSizes(
      ::google::protobuf::io::CodedOutputStream* output) const;
  ::google::protobuf::uint8* SerializeWithCachedSizesToArray(::google::protobuf::uint8* output) const;
  int GetCachedSize() const { return _cached_size_; }
  private:
  void SharedCtor();
  void SharedDtor();
  void SetCachedSize(int size) const;
  public:
  ::google::protobuf::Metadata GetMetadata() const;

  // nested types ----------------------------------------------------

  // accessors -------------------------------------------------------

  // required int32 sampling_rate = 1;
  inline bool has_sampling_rate() const;
  inline void clear_sampling_rate();
  static const int kSamplingRateFieldNumber = 1;
  inline ::google::protobuf::int32 sampling_rate() const;
  inline void set_sampling_rate(::google::protobuf::int32 value);

  // required int32 channels = 2;
  inline bool has_channels() const;
  inline void clear_channels();
  static const int kChannelsFieldNumber = 2;
  inline ::google::protobuf::int32 channels() const;
  inline void set_channels(::google::protobuf::int32 value);

  // @@protoc_insertion_point(class_scope:omnimusic.FormatInfo)
 private:
  inline void set_has_sampling_rate();
  inline void clear_has_sampling_rate();
  inline void set_has_channels();
  inline void clear_has_channels();

  ::google::protobuf::UnknownFieldSet _unknown_fields_;

  ::google::protobuf::uint32 _has_bits_[1];
  mutable int _cached_size_;
  ::google::protobuf::int32 sampling_rate_;
  ::google::protobuf::int32 channels_;
  friend void  protobuf_AddDesc_Plugin_2eproto();
  friend void protobuf_AssignDesc_Plugin_2eproto();
  friend void protobuf_ShutdownFile_Plugin_2eproto();

  void InitAsDefaultInstance();
  static FormatInfo* default_instance_;
};
// -------------------------------------------------------------------

class BufferInfo : public ::google::protobuf::Message {
 public:
  BufferInfo();
  virtual ~BufferInfo();

  BufferInfo(const BufferInfo& from);

  inline BufferInfo& operator=(const BufferInfo& from) {
    CopyFrom(from);
    return *this;
  }

  inline const ::google::protobuf::UnknownFieldSet& unknown_fields() const {
    return _unknown_fields_;
  }

  inline ::google::protobuf::UnknownFieldSet* mutable_unknown_fields() {
    return &_unknown_fields_;
  }

  static const ::google::protobuf::Descriptor* descriptor();
  static const BufferInfo& default_instance();

  void Swap(BufferInfo* other);

  // implements Message ----------------------------------------------

  BufferInfo* New() const;
  void CopyFrom(const ::google::protobuf::Message& from);
  void MergeFrom(const ::google::protobuf::Message& from);
  void CopyFrom(const BufferInfo& from);
  void MergeFrom(const BufferInfo& from);
  void Clear();
  bool IsInitialized() const;

  int ByteSize() const;
  bool MergePartialFromCodedStream(
      ::google::protobuf::io::CodedInputStream* input);
  void SerializeWithCachedSizes(
      ::google::protobuf::io::CodedOutputStream* output) const;
  ::google::protobuf::uint8* SerializeWithCachedSizesToArray(::google::protobuf::uint8* output) const;
  int GetCachedSize() const { return _cached_size_; }
  private:
  void SharedCtor();
  void SharedDtor();
  void SetCachedSize(int size) const;
  public:
  ::google::protobuf::Metadata GetMetadata() const;

  // nested types ----------------------------------------------------

  // accessors -------------------------------------------------------

  // required int32 samples = 1;
  inline bool has_samples() const;
  inline void clear_samples();
  static const int kSamplesFieldNumber = 1;
  inline ::google::protobuf::int32 samples() const;
  inline void set_samples(::google::protobuf::int32 value);

  // required int32 stutter = 2;
  inline bool has_stutter() const;
  inline void clear_stutter();
  static const int kStutterFieldNumber = 2;
  inline ::google::protobuf::int32 stutter() const;
  inline void set_stutter(::google::protobuf::int32 value);

  // @@protoc_insertion_point(class_scope:omnimusic.BufferInfo)
 private:
  inline void set_has_samples();
  inline void clear_has_samples();
  inline void set_has_stutter();
  inline void clear_has_stutter();

  ::google::protobuf::UnknownFieldSet _unknown_fields_;

  ::google::protobuf::uint32 _has_bits_[1];
  mutable int _cached_size_;
  ::google::protobuf::int32 samples_;
  ::google::protobuf::int32 stutter_;
  friend void  protobuf_AddDesc_Plugin_2eproto();
  friend void protobuf_AssignDesc_Plugin_2eproto();
  friend void protobuf_ShutdownFile_Plugin_2eproto();

  void InitAsDefaultInstance();
  static BufferInfo* default_instance_;
};
// ===================================================================


// ===================================================================

// AudioData

// required bytes samples = 1;
inline bool AudioData::has_samples() const {
  return (_has_bits_[0] & 0x00000001u) != 0;
}
inline void AudioData::set_has_samples() {
  _has_bits_[0] |= 0x00000001u;
}
inline void AudioData::clear_has_samples() {
  _has_bits_[0] &= ~0x00000001u;
}
inline void AudioData::clear_samples() {
  if (samples_ != &::google::protobuf::internal::GetEmptyStringAlreadyInited()) {
    samples_->clear();
  }
  clear_has_samples();
}
inline const ::std::string& AudioData::samples() const {
  // @@protoc_insertion_point(field_get:omnimusic.AudioData.samples)
  return *samples_;
}
inline void AudioData::set_samples(const ::std::string& value) {
  set_has_samples();
  if (samples_ == &::google::protobuf::internal::GetEmptyStringAlreadyInited()) {
    samples_ = new ::std::string;
  }
  samples_->assign(value);
  // @@protoc_insertion_point(field_set:omnimusic.AudioData.samples)
}
inline void AudioData::set_samples(const char* value) {
  set_has_samples();
  if (samples_ == &::google::protobuf::internal::GetEmptyStringAlreadyInited()) {
    samples_ = new ::std::string;
  }
  samples_->assign(value);
  // @@protoc_insertion_point(field_set_char:omnimusic.AudioData.samples)
}
inline void AudioData::set_samples(const void* value, size_t size) {
  set_has_samples();
  if (samples_ == &::google::protobuf::internal::GetEmptyStringAlreadyInited()) {
    samples_ = new ::std::string;
  }
  samples_->assign(reinterpret_cast<const char*>(value), size);
  // @@protoc_insertion_point(field_set_pointer:omnimusic.AudioData.samples)
}
inline ::std::string* AudioData::mutable_samples() {
  set_has_samples();
  if (samples_ == &::google::protobuf::internal::GetEmptyStringAlreadyInited()) {
    samples_ = new ::std::string;
  }
  // @@protoc_insertion_point(field_mutable:omnimusic.AudioData.samples)
  return samples_;
}
inline ::std::string* AudioData::release_samples() {
  clear_has_samples();
  if (samples_ == &::google::protobuf::internal::GetEmptyStringAlreadyInited()) {
    return NULL;
  } else {
    ::std::string* temp = samples_;
    samples_ = const_cast< ::std::string*>(&::google::protobuf::internal::GetEmptyStringAlreadyInited());
    return temp;
  }
}
inline void AudioData::set_allocated_samples(::std::string* samples) {
  if (samples_ != &::google::protobuf::internal::GetEmptyStringAlreadyInited()) {
    delete samples_;
  }
  if (samples) {
    set_has_samples();
    samples_ = samples;
  } else {
    clear_has_samples();
    samples_ = const_cast< ::std::string*>(&::google::protobuf::internal::GetEmptyStringAlreadyInited());
  }
  // @@protoc_insertion_point(field_set_allocated:omnimusic.AudioData.samples)
}

// -------------------------------------------------------------------

// AudioResponse

// required int32 written = 1;
inline bool AudioResponse::has_written() const {
  return (_has_bits_[0] & 0x00000001u) != 0;
}
inline void AudioResponse::set_has_written() {
  _has_bits_[0] |= 0x00000001u;
}
inline void AudioResponse::clear_has_written() {
  _has_bits_[0] &= ~0x00000001u;
}
inline void AudioResponse::clear_written() {
  written_ = 0;
  clear_has_written();
}
inline ::google::protobuf::int32 AudioResponse::written() const {
  // @@protoc_insertion_point(field_get:omnimusic.AudioResponse.written)
  return written_;
}
inline void AudioResponse::set_written(::google::protobuf::int32 value) {
  set_has_written();
  written_ = value;
  // @@protoc_insertion_point(field_set:omnimusic.AudioResponse.written)
}

// -------------------------------------------------------------------

// Request

// required .omnimusic.Request.RequestType request = 1;
inline bool Request::has_request() const {
  return (_has_bits_[0] & 0x00000001u) != 0;
}
inline void Request::set_has_request() {
  _has_bits_[0] |= 0x00000001u;
}
inline void Request::clear_has_request() {
  _has_bits_[0] &= ~0x00000001u;
}
inline void Request::clear_request() {
  request_ = 0;
  clear_has_request();
}
inline ::omnimusic::Request_RequestType Request::request() const {
  // @@protoc_insertion_point(field_get:omnimusic.Request.request)
  return static_cast< ::omnimusic::Request_RequestType >(request_);
}
inline void Request::set_request(::omnimusic::Request_RequestType value) {
  assert(::omnimusic::Request_RequestType_IsValid(value));
  set_has_request();
  request_ = value;
  // @@protoc_insertion_point(field_set:omnimusic.Request.request)
}

// -------------------------------------------------------------------

// FormatInfo

// required int32 sampling_rate = 1;
inline bool FormatInfo::has_sampling_rate() const {
  return (_has_bits_[0] & 0x00000001u) != 0;
}
inline void FormatInfo::set_has_sampling_rate() {
  _has_bits_[0] |= 0x00000001u;
}
inline void FormatInfo::clear_has_sampling_rate() {
  _has_bits_[0] &= ~0x00000001u;
}
inline void FormatInfo::clear_sampling_rate() {
  sampling_rate_ = 0;
  clear_has_sampling_rate();
}
inline ::google::protobuf::int32 FormatInfo::sampling_rate() const {
  // @@protoc_insertion_point(field_get:omnimusic.FormatInfo.sampling_rate)
  return sampling_rate_;
}
inline void FormatInfo::set_sampling_rate(::google::protobuf::int32 value) {
  set_has_sampling_rate();
  sampling_rate_ = value;
  // @@protoc_insertion_point(field_set:omnimusic.FormatInfo.sampling_rate)
}

// required int32 channels = 2;
inline bool FormatInfo::has_channels() const {
  return (_has_bits_[0] & 0x00000002u) != 0;
}
inline void FormatInfo::set_has_channels() {
  _has_bits_[0] |= 0x00000002u;
}
inline void FormatInfo::clear_has_channels() {
  _has_bits_[0] &= ~0x00000002u;
}
inline void FormatInfo::clear_channels() {
  channels_ = 0;
  clear_has_channels();
}
inline ::google::protobuf::int32 FormatInfo::channels() const {
  // @@protoc_insertion_point(field_get:omnimusic.FormatInfo.channels)
  return channels_;
}
inline void FormatInfo::set_channels(::google::protobuf::int32 value) {
  set_has_channels();
  channels_ = value;
  // @@protoc_insertion_point(field_set:omnimusic.FormatInfo.channels)
}

// -------------------------------------------------------------------

// BufferInfo

// required int32 samples = 1;
inline bool BufferInfo::has_samples() const {
  return (_has_bits_[0] & 0x00000001u) != 0;
}
inline void BufferInfo::set_has_samples() {
  _has_bits_[0] |= 0x00000001u;
}
inline void BufferInfo::clear_has_samples() {
  _has_bits_[0] &= ~0x00000001u;
}
inline void BufferInfo::clear_samples() {
  samples_ = 0;
  clear_has_samples();
}
inline ::google::protobuf::int32 BufferInfo::samples() const {
  // @@protoc_insertion_point(field_get:omnimusic.BufferInfo.samples)
  return samples_;
}
inline void BufferInfo::set_samples(::google::protobuf::int32 value) {
  set_has_samples();
  samples_ = value;
  // @@protoc_insertion_point(field_set:omnimusic.BufferInfo.samples)
}

// required int32 stutter = 2;
inline bool BufferInfo::has_stutter() const {
  return (_has_bits_[0] & 0x00000002u) != 0;
}
inline void BufferInfo::set_has_stutter() {
  _has_bits_[0] |= 0x00000002u;
}
inline void BufferInfo::clear_has_stutter() {
  _has_bits_[0] &= ~0x00000002u;
}
inline void BufferInfo::clear_stutter() {
  stutter_ = 0;
  clear_has_stutter();
}
inline ::google::protobuf::int32 BufferInfo::stutter() const {
  // @@protoc_insertion_point(field_get:omnimusic.BufferInfo.stutter)
  return stutter_;
}
inline void BufferInfo::set_stutter(::google::protobuf::int32 value) {
  set_has_stutter();
  stutter_ = value;
  // @@protoc_insertion_point(field_set:omnimusic.BufferInfo.stutter)
}


// @@protoc_insertion_point(namespace_scope)

}  // namespace omnimusic

#ifndef SWIG
namespace google {
namespace protobuf {

template <> struct is_proto_enum< ::omnimusic::Request_RequestType> : ::google::protobuf::internal::true_type {};
template <>
inline const EnumDescriptor* GetEnumDescriptor< ::omnimusic::Request_RequestType>() {
  return ::omnimusic::Request_RequestType_descriptor();
}

}  // namespace google
}  // namespace protobuf
#endif  // SWIG

// @@protoc_insertion_point(global_scope)

#endif  // PROTOBUF_Plugin_2eproto__INCLUDED
